public class Message {
    //have,request
    public static byte[] createActualMessage(byte type,int payload){
        byte[] payload_byte=intToByteArray(payload);
        int len=payload_byte.length + 1;
        byte[] len_byte=intToByteArray(len);
        return byteMerger(len_byte,type,payload_byte);

    }
    //piece, bitfield
    public static byte[] createActualMessage(byte type,byte[] payload){
        int len=payload.length + 1;
        byte[] len_byte=intToByteArray(len);
        return byteMerger(len_byte,type,payload);

    }
    //choke,unchoke,interested,not interested
    public static byte[] createActualMessage(byte type){
        byte[] len_byte=intToByteArray(1);
        return byteMerger(len_byte,type);

    }

    public static byte readMsgType(byte[] msg){
        byte type=msg[0];
        return type;
    }
    public static byte[] readMsgPayload(byte[] msg){
//        int len=msg.length;
//        byte[] payload = new byte[len-1];
//        System.arraycopy(msg, 1, payload, 0, len);
//        return payload;
    	return msg;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
    public static byte[] byteMerger(byte[] bt1, byte bt2, byte[] bt3){
        byte[] bt = new byte[bt1.length+1+bt3.length];
        System.arraycopy(bt1, 0, bt, 0, bt1.length);
        bt[bt1.length]=bt2;
        System.arraycopy(bt3, 0, bt, bt1.length+1, bt3.length);
        return bt;
    }
    public static byte[] byteMerger(byte[] bt1, byte bt2){
        byte[] bt = new byte[bt1.length+1];
        System.arraycopy(bt1, 0, bt, 0, bt1.length);
        bt[bt1.length]=bt2;
        return bt;
    }
    public static byte[] byteMerger(byte[] bt1, byte[] bt3){
        byte[] bt = new byte[bt1.length+bt3.length];
        System.arraycopy(bt1, 0, bt, 0, bt1.length);
        System.arraycopy(bt3, 0, bt, bt1.length, bt3.length);
        return bt;
    }

    public static void main(String[] args) {
//        Message msg=new Message();
//        byte[] b={1,2,3,4,5,6,7,8};
//        byte[] b2=msg.readMsgPayload(b);
//        for(byte i:b2){
//            System.out.println(i);
//        }

    }
}


