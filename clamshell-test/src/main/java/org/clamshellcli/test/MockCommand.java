/*
 * Copyright 2012 ClamShell-Cli.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clamshellcli.test;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;

/**
 *
 * @author vvivien
 */
public class MockCommand implements Command{

    @Override
    public Descriptor getDescriptor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object execute(Context ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void plug(Context plug) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unplug(Context plug) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
