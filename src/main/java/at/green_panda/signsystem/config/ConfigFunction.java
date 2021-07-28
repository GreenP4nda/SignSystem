package at.green_panda.signsystem.config;

import java.util.List;

/**
 * Created by Green_Panda
 * Class create at 28.07.2021 09:46
 */

public class ConfigFunction {
    String signFunctionType;
    String function;

    public ConfigFunction(String signFunctionType, String function) {
        this.signFunctionType = signFunctionType;
        this.function = function;
    }

    public String getSignFunctionType() {
        return signFunctionType;
    }
    public String getFunction() {
        return function;
    }
}
