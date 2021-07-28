package at.green_panda.signsystem.config;

import at.green_panda.signsystem.api.ClickableSign;

import java.util.List;

/**
 * Created by Green_Panda
 * Class create at 28.07.2021 12:22
 */

public class ConfigSignSave {
    List<ConfigSign> clickableSigns;

    public ConfigSignSave(List<ConfigSign> clickableSigns) {
        this.clickableSigns = clickableSigns;
    }

    public List<ConfigSign> getClickableSigns() {
        return clickableSigns;
    }
}
