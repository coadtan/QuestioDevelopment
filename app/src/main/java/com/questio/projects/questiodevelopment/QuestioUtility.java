package com.questio.projects.questiodevelopment;

import android.util.Log;

import com.questio.projects.questiodevelopment.models.ZoneObject;

import java.util.ArrayList;

public class QuestioUtility {


    private static final String LOG_TAG = QuestioUtility.class.getSimpleName();

    // passing "questio:zone:123:questio" to paremater qrCode will return "123"
    public static String getDeQRCode(String qrCode) {
        String deCodeForReturn = "failed";
        final String BEGIN_TYPE_1 = "questio:zone:";
        final String BEGIN_TYPE_2 = "questio:place:";
        final String END = ":questio";
        // 1 Step: check size
        if (qrCode.length() < (BEGIN_TYPE_2.length()+END.length())){
            return deCodeForReturn;
        }
        // 2 Step: removeBegin
        String type = getQRType(qrCode);

        if(type.equalsIgnoreCase("place")){
            qrCode = qrCode.replace(BEGIN_TYPE_2,"");
        }else if(type.equalsIgnoreCase("zone")){
            qrCode = qrCode.replace(BEGIN_TYPE_1,"");
        }

        // 3 Step: removeEnd
        qrCode = qrCode.replace(END,"");
        deCodeForReturn = qrCode;
        return deCodeForReturn;
    }

    public static String getQRType(String qrCode){
        String type = qrCode.substring(8,9);
        if(type.equalsIgnoreCase("p")){
            type="place";
        }else if(type.equalsIgnoreCase("z")){
            type="zone";
        }else{
            type="N/A";
        }
        return type;
    }

    public static int checkValidQRCode(String qrCode,ArrayList<ZoneObject> zoneList){
        int count = -1;
        String tempQR;
        for(int i = 0; i < zoneList.size(); i++){
            tempQR = zoneList.get(i).getQrCode();
            if(tempQR.equalsIgnoreCase(qrCode)){
                count = i;
                return count;
            }
        }
        Log.d(LOG_TAG, "checkValidQRCode: QRCode Invalid: Not found in list or database.");
        return count;
    }

}
