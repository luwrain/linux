/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.base.hardware.*;
import org.luwrain.core.*;

final class SysDeviceImpl implements SysDevice
{
    private final Type type;
    private final String id;
    private final String cls;
    private final String vendor;
    private final String model;
    private final String driver;
    private final String module;

    SysDeviceImpl(Type type,
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

    @Override public Type getDevType()
    {
	return type;
    }

    @Override public String getDevId()
    {
	return id;
    }

    @Override public String getDevClass()
    {
	return cls;
    }

    @Override public String getDevVendor()
    {
	return vendor;
    }

    @Override public String getDevModel()
    {
	return model;
    }

    @Override public String getDevDriver()
    {
	return driver;
    }

    @Override public String getDevModule()
    {
	return module;
    }
}
