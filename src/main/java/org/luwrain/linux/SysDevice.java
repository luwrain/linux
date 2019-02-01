/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.linux;

import org.luwrain.core.*;

public final class SysDevice
{
    public enum Type {
	UNKNOWN,
	PCI,
	USB,
    };

    public final Type type;
    public final String id;
    public final String cls;
    public final String vendor;
    public final String model;
    public final String driver;
    public final String module;

    SysDevice(Type type,
		  String id,
		  String cls,
		  String vendor,
		  String model,
		  String driver,
		  String module)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(id, "id");
	NullCheck.notNull(cls, "cls");
	NullCheck.notNull(vendor, "vendor");
	NullCheck.notNull(model, "model");
	NullCheck.notNull(driver, "driver");
	NullCheck.notNull(module, "module");
	this.type = type;
	this.id = id;
	this.cls = cls;
	this.vendor = vendor;
	this.model = model;
	this.driver = driver;
	this.module = module;
    }
}
