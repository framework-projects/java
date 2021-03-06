/*
 * Copyright (c) 2011, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.tools.jcmd;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AttachOperationFailedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.jvmstat.monitor.*;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.common.ProcessArgumentMatcher;
import sun.tools.jstat.JStatLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Comparator;

public class JCmd {
    public static void main(String[] args) {
        Arguments arg = null;
        try {
            arg = new Arguments(args);
        } catch (IllegalArgumentException ex) {
            System.err.println("Error parsing arguments: " + ex.getMessage()
                               + "\n");
            Arguments.usage();
            System.exit(1);
        }

        if (arg.isShowUsage()) {
            Arguments.usage();
            System.exit(0);
        }

        ProcessArgumentMatcher ap = null;
        try {
            ap = new ProcessArgumentMatcher(arg.getProcessString());
        } catch (IllegalArgumentException iae) {
            System.err.println("Invalid pid '" + arg.getProcessString()  + "' specified");
            System.exit(1);
        }

        if (arg.isListProcesses()) {
            for (VirtualMachineDescriptor vmd : ap.getVirtualMachineDescriptors(/* include jcmd in listing */)) {
                System.out.println(vmd.id() + " " + vmd.displayName());
            }
            System.exit(0);
        }

        Collection<String> pids = ap.getVirtualMachinePids(JCmd.class);

        if (pids.isEmpty()) {
            System.err.println("Could not find any processes matching : '"
                               + arg.getProcessString() + "'");
            System.exit(1);
        }

        boolean success = true;
        for (String pid : pids) {
            System.out.println(pid + ":");
            if (arg.isListCounters()) {
                listCounters(pid);
            } else {
                try {
                    executeCommandForPid(pid, arg.getCommand());
                } catch(AttachOperationFailedException ex) {
                    System.err.println(ex.getMessage());
                    success = false;
                } catch(Exception ex) {
                    ex.printStackTrace();
                    success = false;
                }
            }
        }
        System.exit(success ? 0 : 1);
    }

    private static void executeCommandForPid(String pid, String command)
        throws AttachNotSupportedException, IOException,
               UnsupportedEncodingException {
        VirtualMachine vm = VirtualMachine.attach(pid);

        // Cast to HotSpotVirtualMachine as this is an
        // implementation specific method.
        HotSpotVirtualMachine hvm = (HotSpotVirtualMachine) vm;
        String lines[] = command.split("\\n");
        for (String line : lines) {
            if (line.trim().equals("stop")) {
                break;
            }
            try (InputStream in = hvm.executeJCmd(line);) {
                // read to EOF and just print output
                byte b[] = new byte[256];
                int n;
                boolean messagePrinted = false;
                do {
                    n = in.read(b);
                    if (n > 0) {
                        String s = new String(b, 0, n, "UTF-8");
                        System.out.print(s);
                        messagePrinted = true;
                    }
                } while (n > 0);
                if (!messagePrinted) {
                    System.out.println("Command executed successfully");
                }
            }
        }
        vm.detach();
    }

    private static void listCounters(String pid) {
        // Code from JStat (can't call it directly since it does System.exit)
        VmIdentifier vmId = null;
        try {
            vmId = new VmIdentifier(pid);
        } catch (URISyntaxException e) {
            System.err.println("Malformed VM Identifier: " + pid);
            return;
        }
        try {
            MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
            MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, -1);
            JStatLogger logger = new JStatLogger(monitoredVm);
            logger.printSnapShot("\\w*", // all names
                    new AscendingMonitorComparator(), // comparator
                    false, // not verbose
                    true, // show unsupported
                    System.out);
            monitoredHost.detach(monitoredVm);
        } catch (MonitorException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Class to compare two Monitor objects by name in ascending order.
     * (from jstat)
     */
    static class AscendingMonitorComparator implements Comparator<Monitor> {

        public int compare(Monitor m1, Monitor m2) {
            String name1 = m1.getName();
            String name2 = m2.getName();
            return name1.compareTo(name2);
        }
    }
}
