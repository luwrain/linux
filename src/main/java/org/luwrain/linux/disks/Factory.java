
package org.luwrain.linux.disks;

import org.luwrain.core.*;
import org.luwrain.popups.*;

public final class Factory implements DisksPopup.Factory
{
    @Override public Disks newDisks(Luwrain luwrain)
    {
	return new Disks();
    }
}
