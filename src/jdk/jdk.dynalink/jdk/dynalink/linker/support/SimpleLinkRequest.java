/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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

/*
 *
 *
 *
 *
 *
 */
/*
   Copyright 2009-2013 Attila Szegedi

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
   * Neither the name of the copyright holder nor the names of
     contributors may be used to endorse or promote products derived from
     this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
   IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
   TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
   PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
   BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package jdk.dynalink.linker.support;

import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.linker.LinkRequest;

import java.util.Objects;

/**
 * Default simple implementation of {@link LinkRequest}.
 */
public class SimpleLinkRequest implements LinkRequest {

    private final CallSiteDescriptor callSiteDescriptor;
    private final Object[] arguments;
    private final boolean callSiteUnstable;

    /**
     * Creates a new link request.
     *
     * @param callSiteDescriptor the descriptor for the call site being linked.
     * Must not be null.
     * @param callSiteUnstable true if the call site being linked is considered
     * unstable.
     * @param arguments the arguments for the invocation. Must not be null.
     * @throws NullPointerException if either {@code callSiteDescriptor} or
     * {@code arguments} is null.
     */
    public SimpleLinkRequest(final CallSiteDescriptor callSiteDescriptor, final boolean callSiteUnstable, final Object... arguments) {
        this.callSiteDescriptor = Objects.requireNonNull(callSiteDescriptor);
        this.callSiteUnstable = callSiteUnstable;
        this.arguments = arguments.clone();
    }

    @Override
    public Object[] getArguments() {
        return arguments.clone();
    }

    @Override
    public Object getReceiver() {
        return arguments.length > 0 ? arguments[0] : null;
    }

    @Override
    public CallSiteDescriptor getCallSiteDescriptor() {
        return callSiteDescriptor;
    }

    @Override
    public boolean isCallSiteUnstable() {
        return callSiteUnstable;
    }

    @Override
    public LinkRequest replaceArguments(final CallSiteDescriptor newCallSiteDescriptor, final Object... newArguments) {
        return new SimpleLinkRequest(newCallSiteDescriptor, callSiteUnstable, newArguments);
    }
}
