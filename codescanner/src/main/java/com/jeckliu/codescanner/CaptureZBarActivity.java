package com.jeckliu.codescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jeckliu.framwork.base.BaseActivity;
import com.jeckliu.framwork.base.BaseWebActivity;
import com.jeckliu.framwork.permission.IPermission;
import com.jeckliu.framwork.view.DenyPermissionDialog;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import java.util.List;

public class CaptureZBarActivity extends BaseActivity {
    public static final String EXTRA_ZOOM = "Zoom";
    public static final String EXTRA_CROP = "crop";
    public static final String EXTRA_OPTIMALSIZETYPE = "optimalSizeType";
    private int zoom = -1;
    private boolean isCrop = true;
    private int optimalSizeType = 0;

    private Camera mCamera;
    private CameraPreview mPreview;


    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private Button btnFlashLighting;
    private LinearLayout llFloatingMsg;
    private TextView tvLocalPhoto;
    private ViewfinderView viewfinderView;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zbar_preview_layout);

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        tvLocalPhoto = (TextView) findViewById(R.id.titleBar_right_text);
        llFloatingMsg = (LinearLayout) findViewById(R.id.zbar_floating_show_msg);
        btnFlashLighting = (Button) findViewById(R.id.zbar_flash_lighting);
        zoom = getIntent().getIntExtra(EXTRA_ZOOM, -1);
        isCrop = getIntent().getBooleanExtra(EXTRA_CROP, true);
        optimalSizeType = getIntent().getIntExtra(EXTRA_OPTIMALSIZETYPE, 0);
        permissionAction.addPermission(Manifest.permission.CAMERA)
                .requestPermission(new IPermission() {
                    @Override
                    public void done() {
                        mPreview = new CameraPreview(CaptureZBarActivity.this, previewCb, zoom, optimalSizeType);
                        mCamera = mPreview.getCamera();
                        viewfinderView.setCamera(mCamera);
                        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
                        preview.addView(mPreview, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    }

                    @Override
                    public void unPermission(List<String> denyPermissions) {
                        DenyPermissionDialog.show(CaptureZBarActivity.this,denyPermissions);
                    }
                });

        tvLocalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        llFloatingMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeScanned = false;
                viewfinderView.startScan();
                llFloatingMsg.setVisibility(View.GONE);
            }
        });
    }

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();
            if (scannerRun == null && !barcodeScanned) {
                scannerRun = new ScannerRun(data, size);
                new Thread(scannerRun).start();
            }
        }
    };

    public void setDrawText(String drawText) {
        if (viewfinderView != null) {
            viewfinderView.setDrawText(drawText);
        }
    }

    public void onBack(View view) {
        onBackPressed();
    }


    private ScannerRun scannerRun;

    private class ScannerRun implements Runnable {
        Image barcode;
        byte[] data;
        Size size;

        ScannerRun(byte[] data, Size size) {
            this.data = data;
            this.size = size;
        }

        private void setCrop() {
            int cameraWidth = size.width;
            int cameraHeight = size.height;
            int cropArea = cameraHeight * 5 / 8;
            int left = (cameraWidth - cropArea) * 4 / 13;
            int top = (cameraHeight - cropArea) / 2;
            barcode.setCrop(left, top, cropArea, cropArea);
        }

        public void run() {
            barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            if (isCrop) {
                setCrop();
            }

            int result = scanner.scanImage(barcode);
            if (result != 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SymbolSet syms = scanner.getResults();
                        for (Symbol sym : syms) {
                            if (!TextUtils.isEmpty(sym.getData())) {
                                Intent intent = new Intent(getBaseContext(), BaseWebActivity.class);
                                intent.putExtra(BaseWebActivity.URL, sym.getData());
                                startActivity(intent);
                            } else {
                                viewfinderView.cancelScan();
                                llFloatingMsg.setVisibility(View.VISIBLE);
                            }
                            barcodeScanned = true;
                            break;
                        }
                        scannerRun = null;
                    }
                });
            } else {
                scannerRun = null;
            }
        }
    }

    public void onOpenLight(View view) {

        mCamera = mPreview.getCamera();
        if (!checkFlashlight()) {
            return;
        }
        if (mCamera == null) {
            return;
        }
        Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null) {
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            btnFlashLighting.setBackgroundDrawable(getResources().getDrawable(R.drawable.lighting_open));
        } else {
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            btnFlashLighting.setBackgroundDrawable(getResources().getDrawable(R.drawable.lighting_close));
        }
    }

    // 检测当前设备是否配置闪光灯
    private boolean checkFlashlight() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
