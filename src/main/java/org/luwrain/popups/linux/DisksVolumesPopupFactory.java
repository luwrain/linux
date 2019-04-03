
package org.luwrain.popups.linux;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public final class DisksVolumesPopupFactory implements org.luwrain.popups.DisksVolumesPopupFactory
{
    @Override public org.luwrain.popups.DisksVolumesPopup newDisksVolumesPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(popupFlags, "popupFlags");
	return new DisksVolumesPopup(luwrain, name, popupFlags);
    }
}
