package com.b44t.messenger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRshowActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrshow);

        ImageView imageView = (ImageView) findViewById(R.id.myImage);
        try {
            Bitmap bitmap = encodeAsBitmap(MrMailbox.getSecurejoinQr(0));
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.secureJoinRequested);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.secureJoinRequested);
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if( id==NotificationCenter.secureJoinRequested ) {
            int contact_id = (Integer)args[0];
            String msg = String.format(ApplicationLoader.applicationContext.getString(R.string.OobSecureJoinRequested), MrMailbox.getContact(contact_id).getNameNAddr());
            AndroidUtilities.showHint(ApplicationLoader.applicationContext, msg);
        }
    }

}
