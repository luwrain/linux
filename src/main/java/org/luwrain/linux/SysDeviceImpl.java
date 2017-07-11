
package org.luwrain.linux;

import org.luwrain.base.*;
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
