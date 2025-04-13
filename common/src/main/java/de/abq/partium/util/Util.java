package de.abq.partium.util;
import net.minecraft.resources.ResourceLocation;

public class Util {
    public static final ResourceLocation EMPTY_RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath("", "");

    public static int HexStringToIntRGB(String hexString){
        if (hexString.length() < 7) return 0x000000;
        return Integer.parseInt(hexString.substring(1,7), 16);
    }
    public static int addAlphaARGB(int rgb, int alpha){
        return (alpha << 24) + rgb;
    }
    public static int HexStringToIntARGB(String hexString, int alpha){
        return addAlphaARGB(HexStringToIntRGB(hexString), alpha);
    }
    public static int HexStringToIntARGB(String hexString){
        return addAlphaARGB(HexStringToIntRGB(hexString), 0xff);
    }
}
