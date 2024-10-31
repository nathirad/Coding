package jp.jaxa.iss.kibo.rpc.sampleapk;

        import android.util.Log;

        import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Collections;
        import java.util.Arrays;
        import gov.nasa.arc.astrobee.Result;
        import gov.nasa.arc.astrobee.android.gs.MessageType;
        import gov.nasa.arc.astrobee.types.Point;
        import gov.nasa.arc.astrobee.types.Quaternion;

        import android.util.Log;

        import org.opencv.core.*;
        import org.opencv.imgcodecs.Imgcodecs;
        import org.opencv.objdetect.QRCodeDetector;


        import org.opencv.core.Mat;
        import org.opencv.core.Rect;


//import java.nio.ByteBuffer;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */


public class YourService extends KiboRpcService {

    final static Point  point_start_2 = new Point(10.6f, -9.37f, 4.48f);
    final static Quaternion quaternion_start_2 = new Quaternion(1.f, 0f, 0f, 0f);
    final static Point  point_idle = new Point(10.612f, -9.0709f, 5.3);
    final static Point point1 = new Point(11.2746f-0.064, -9.92284f, 5.2988f+0.1812f);
    final static Quaternion quaternion1 = new Quaternion(0.f, 0f, -0.707f, 0.707f);
    final static Point point2 = new Point(10.612f-0.152f, -9.0709f-0.1191f, 4.48f);
    final static Quaternion quaternion2 = new Quaternion(0.5f, 0.5f, -0.5f, 0.5f);
    final static Point point3 = new Point(10.71f, -7.7f-0.05f, 4.48f);
    final static Quaternion quaternion3 = new Quaternion(0f, 0.707f, 0f, 0.707f);
    final static Point point4 = new Point(10.51f, -6.7185f+0.10f, 5.1804f+0.05);
    final static Quaternion quaternion4 = new Quaternion(0f, 0f, -1f, 0f);
    final static Point point5 = new Point(11.114f-0.05f, -7.9756f+0.05f, 5.3393f);
    final static Quaternion quaternion5 = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
    final static Point point6 = new Point(11.355f, -8.9929f-0.05f, 4.7818f+0.15);
    final static Quaternion quaternion6 = new Quaternion(0f, 0f, 0f, 1f);
    final static Point point7 = new Point(11.369f, -8.5518f, 4.48f);
    final static Quaternion quaternion7 = new Quaternion(0f, 0.707f, 0f, 0.707f);
    final static Point point_goal = new Point(11.143f, -6.7607f, 4.9654);
    final static Quaternion quaternion_goal = new Quaternion(0f, 0f,-0.707f,0.707f);
    static List[][] middle_point = new ArrayList[9][9];
    static Mat img_qr=null, img_roi=null;
    static Integer phase_no =0;
    static boolean game_start = false;
    static Boolean complete_phase = false;
    static Boolean default_phase_time_up = false;
    static Long next_end_time = 300000L;
    final static Long PHASE_LIMIT = 120000L;
    final static Long ALL_TIME_LIMIT = 300000L;
    static List<Long> time_remaining_at_complete_phase = new ArrayList<>(Arrays.asList(0L,0L)) ;
    static int shot7_cnt =0;

    public class QRCodeReader {
        private QRCodeDetector qrCodeDetector;

        public QRCodeReader() {
            qrCodeDetector = new QRCodeDetector();
        }

        public String readQRCode(Mat img) {
            Mat image = img;
            MatOfPoint2f points = new MatOfPoint2f();
            try {
                String data = qrCodeDetector.detectAndDecode(image, points);

                if (!data.isEmpty()) {
                    return data;
                } else {
                    return null;
                }
            }catch(Exception e){
                Log.i("Fail QR Decode: ","");
                return null;
            }
        }

    }

    protected void save_img(String file_name, Mat img){
        // TODO : select one of below later
        api.saveMatImage(img, file_name );
        //Imgcodecs.imwrite(file_name, img);
        // ------------------------------------------------//
    }
    protected void delay(int ms){
        try {
            Thread.sleep(ms);
            // calTargetPos(1);
        } catch (Exception e) {

        }
    }
    protected void save_img_laser(String file_name, int delay_ms) {
        api.laserControl(true);
        delay(delay_ms);
        save_img( file_name, api.getMatNavCam());
        api.laserControl(false);
    }
    protected void save_img_snapshot(String file_name,int delay_ms,Integer qid){
        api.laserControl(true);
        delay(delay_ms);
        save_img( file_name, api.getMatNavCam());
        api.takeTargetSnapshot(qid);
    }
    protected  Point get_point(Integer x) {
        switch(x){
            case 0: return point_start_2;
            case 1: return point1;
            case 2: return point2;
            case 3: return point3;
            case 4: return point4;
            case 5: return point5;
            case 6: return point6;
            case 7: return point7;
            case 8: return point_goal;
        }
        return point_idle;
    }
    protected  Quaternion get_Quaternion(Integer x) {
        switch(x){
            case 0: return quaternion_start_2;
            case 1: return quaternion1;
            case 2: return quaternion2;
            case 3: return quaternion3;
            case 4: return quaternion4;
            case 5: return quaternion5;
            case 6: return quaternion6;
            case 7: return quaternion7;
            case 8: return quaternion_goal;
        }
        return quaternion_goal;
    }
    protected List[][] middle_point_mapping(){

        List[][]  mid_point = new ArrayList[9][9];
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++ ){
                mid_point[i][j] = new ArrayList<Point>();
            }
        }
        // start -> point1
        mid_point[0][1].add(0,point_start_2);
        mid_point[0][1].add(1,new Point(11.0f, -9.0f, 5.3f));
        // start -> point2
        mid_point[0][2].add(0,point_start_2);
        // start -> point3
        mid_point[0][3].add(0,point_start_2);
        mid_point[0][3].add(1,new Point(point2.getX(), point2.getY(), 4.67+0.18));
        mid_point[0][3].add(2,new Point(10.5f, -7.5f, 5.3f));
        // start -> point4
        mid_point[0][4].add(0,point_start_2);
        mid_point[0][4].add(1,new Point(point2.getX(), point2.getY(), 4.82+0.18));
        // start -> point5
        mid_point[0][5] = mid_point[0][3];
        // start -> point6
        //mid_point[0][6].add(0,point6); || --->*
        //mid_point[0][6].add(0, point_start_2);  |-->use this
        //mid_point[0][6].add(1 ,new Point( 10.96, -9.37f ,5.0f));
        //mid_point[0][6].add(1 ,new Point( point6.getX(), point6.getY(),5.0f));
        // start -> point7
        //mid_point[0][7].add(0 ,point_start_2); |-->
        //mid_point[0][7].add(1 ,point6);        |-->
        //mid_point[0][7].add(2 ,new Point( point7.getX(), point7.getY() ,5.0f)); |-->
        mid_point[0][7].add(0, point6);


        // point1 -> point2
        mid_point[1][2].add(0,mid_point[0][1].get(1));
        //mid_point[1][2].add(1,mid_point[0][1].get(0)); |-->*

        // point1 -> point3
        mid_point[1][3].add(0,mid_point[0][3].get(2));
        // point1 -> point4
        mid_point[1][4]= mid_point[1][3];
        // point1 -> point5
        // mid_point[1][5].add(0, point1 /*mid_point[0][3].get(2)*/ ); // |-->*
        // point1 -> point6
        // mid_point[1][6].add(0, point1); // |-->*
        // point1 -> point7
        //mid_point[1][7].add(0, new Point( point7.getX(), point7.getY() ,4.85) /*mid_point[0][1].get(1)*/);  // |-->*
        // point1 -> goal
        mid_point[1][8].add(0, new Point(point_goal.getX(),point_goal.getY(),5.4));
        //mid_point[1][8]= mid_point[1][3]; // |-->*
        //mid_point[1][8].add(1, point4);


        // point2 -> point3
        mid_point[2][3].add(0, mid_point[0][3].get(1));
        mid_point[2][3].add(1, mid_point[0][3].get(2));
        // point2 -> point4
        mid_point[2][4].add(0, mid_point[0][4].get(1));
        mid_point[2][4].add(1, new Point(point4.getX(), point4.getY(),5.4f));
        // point2 -> point5
        mid_point[2][5]= mid_point[2][3];
        // point2 -> point6
        mid_point[2][6].add(0, new Point( 10.96, -9.37f ,5.0f));
        // point2 -> point7
        mid_point[2][7].add(0, new Point( 10.96, -9.37f ,5.0f));
        mid_point[2][7].add(1, new Point( point7.getX(), point7.getY(), 5.0f));
        // point2 -> goal
        mid_point[2][8].add(0, new Point(point2.getX(), point2.getY(), 5.3));
        mid_point[2][8].add(1, mid_point[0][3].get(2));


        // point3 -> point4
        mid_point[3][4].add(0, mid_point[0][3].get(2) );
        // point3 -> point5
        mid_point[3][5].add(0, mid_point[0][3].get(2) );
        // point3 -> point6
        mid_point[3][6].add(0, mid_point[0][3].get(2) );
        mid_point[3][6].add(1, mid_point[0][1].get(1) );
        // point3 -> point7
        mid_point[3][7].add(0, mid_point[0][3].get(2) );
        mid_point[3][7].add(1, mid_point[0][1].get(1) );
        // point3 -> goal
        mid_point[3][8].add(0, mid_point[0][3].get(2));


        // point4 -> point5
        mid_point[4][5].add(0, mid_point[0][3].get(2) );
        // point4 -> point6
        mid_point[4][6].add(0,mid_point[0][3].get(1));
        mid_point[4][6].add(1,new Point(point6.getX(),point6.getY(),5.3));
        // point4 -> point7
        //mid_point[4][7].add(0,mid_point[0][3].get(1));
        //mid_point[4][7].add(0,mid_point[0][3].get(1));
        mid_point[4][7].add(0,new Point(point4.getX(),point4.getY(),5.4));
        mid_point[4][7].add(1,new Point(point7.getX(),point7.getY(),5.4));
        // point4 -> goal
        //mid_point[4][8].add(point4); // |-->*


        // point5 -> point6
        mid_point[5][6].add(0, mid_point[0][1].get(1) );
        // point5 -> point7
        mid_point[5][7]=mid_point[5][6];
        // point5 -> goal
        mid_point[5][8].add( 0, mid_point[0][3].get(2) );


        // point6-> point7
        // mid_point[6][7].add(0, point6); // |-->*
        // point6-> goal
  