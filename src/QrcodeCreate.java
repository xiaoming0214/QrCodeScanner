import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by bujue on 16/11/25.
 */
public class QrcodeCreate {

    /**
     * 读取Excel文件
     *
     * @param excelFile
     */
    public static ArrayList<BusinessEntity> readExcel(String excelFile) {
        System.out.println("开始读取Excel文件,FileName:"+excelFile+"...");
        File f = new File(excelFile);
        if(f == null || !f.exists()){
            System.out.println("!!!文件不存在!!!");
            return new ArrayList<>();
        }
        try {
            Workbook book = Workbook.getWorkbook(f);

            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            System.out.println("开始读取...");
            ArrayList<BusinessEntity> list = new ArrayList<BusinessEntity>();
            //获得单元格
            for(int i = 1; i < sheet.getRows(); i++){
                Cell cell = sheet.getCell(0,i);
                Cell cell1 = sheet.getCell(2,i);
                Cell cell2 = sheet.getCell(5,i);

                System.out.println(">>>商户号 = "+ cell.getContents());
                System.out.println(">>>商户名称 = "+ cell1.getContents());
                System.out.println(">>>商户二维码 = "+ cell2.getContents());
                BusinessEntity businessEntity = new BusinessEntity();
                businessEntity.code = cell.getContents();
                businessEntity.name = cell1.getContents();
                businessEntity.qrcode = cell2.getContents();
                list.add(businessEntity);
                System.out.println("------------------------------");
            }
            System.out.println("读取完成...");
            return list;
        } catch (BiffException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 开始创建图片
     * @param list
     */
    public static void createBitmap(ArrayList<BusinessEntity> list){
        System.out.println("删除原缓存的二维码图片...");
        FileUtil.deleteFile("./temp");
        FileUtil.deleteFile("./result");
        System.out.println("开始生成图片...");
        if(list == null || list.size() == 0){
            System.out.println("生成图片失败,输入源为空...");
            return;
        }

        System.out.println("开始生成二维码...");
        for(BusinessEntity entity : list){
            System.out.println("商户名称:"+entity.name+">>开始生成二维码...");
            try {
                String qrcodePath = QrcodeUtil.encode(entity.qrcode,"./temp");
                System.out.println("商户名称:"+entity.name+">>生成二维码完成...");

                String realQrcodePath = "./temp/"+qrcodePath;

                if(realQrcodePath != null && !"".equals(realQrcodePath)) {
                    // 水印操作
                    String waterMarkPath = waterMark(entity,realQrcodePath);
                    // 两张图片合成
                    createPicTwo2("./origin/test.png", waterMarkPath, entity);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("商户名称:"+entity.name+">>生成二维码失败...");
            }
        }
        FileUtil.deleteFile("./temp");
        System.out.println("生成二维码结束...");
        System.out.println("End....");
    }

    /**
     * 生成水印
     * @param entity
     * @param ImgName
     * @return
     */
    public static String waterMark(BusinessEntity entity, String ImgName) {
        System.out.println("商户名称:"+entity.name+">>开始生成水印");
        byte[] bytes = null;
        try {
            File _file = new File(ImgName);
            Image src = ImageIO.read(_file);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            g.setColor(Color.black);
            g.setFont(new Font("宋体", Font.BOLD, 25));

            String code = entity.code;
            String name = entity.name;

            int nameLength = (name.length() * 22)/2;
            int codeLength = (code.length() * 12)/2;

            g.drawString(name, wideth/2 - nameLength, height-5);
            g.drawString(code, wideth/2 - codeLength, 22);

            g.dispose();
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            saveImage(image, out1);
            bytes = out1.toByteArray();
            out1.close();

            String destImgName = "./temp/"+System.currentTimeMillis()+".jpg";
            FileOutputStream out2 = new FileOutputStream(destImgName);
            out2.write(bytes);
            out2.close();
            System.out.println("商户名称:"+entity.name+">>产生水印成功");
            return destImgName;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("商户名称:"+entity.name+">>产生水印失败");
            return "";
        }
    }

    public static void saveImage(BufferedImage img, OutputStream out1) throws Exception {
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out1);
        encoder.encode(img);
    }

    public static void createPicTwo2(String srcImgPath,String srcImgPath2,BusinessEntity entity) {
        System.out.println("商户名称:"+entity.name+">>开始合成图片...");
        try {
            QrcodeUtil.mkdirs("./result");
            //读取第一张图片
            File fileOne = new File(srcImgPath);
            BufferedImage ImageOne = ImageIO.read(fileOne);
            int width = ImageOne.getWidth();//图片宽度
            int height = ImageOne.getHeight();//图片高度
            //从图片中读取RGB
            int[] ImageArrayOne = new int[width * height];
            ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne, 0, width);

            //对第二张图片做相同的处理
            File fileTwo = new File(srcImgPath2);
            BufferedImage ImageTwo = ImageIO.read(fileTwo);
            int widthTwo = ImageTwo.getWidth();//图片宽度
            int heightTwo = ImageTwo.getHeight();//图片高度
            int[] ImageArrayTwo = new int[widthTwo * heightTwo];
            ImageArrayTwo = ImageTwo.getRGB(0, 0, widthTwo, heightTwo, ImageArrayTwo, 0, widthTwo);


            //生成新图片
            BufferedImage ImageNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            ImageNew.setRGB(0, 0, width, height, ImageArrayOne, 0, width);//设置左半部分的RGB
            ImageNew.setRGB(420, 370, widthTwo, heightTwo, ImageArrayTwo, 0, widthTwo);//设置右半部分的RGB
//            ImageNew.setRGB(x*2,y,widthTwo,heightTwo,ImageArrayTwo,0,widthTwo);//设置右半部分的RGB
            File outFile = new File("./result/"+entity.code+"+"+entity.name+".jpg");
            ImageIO.write(ImageNew, "jpg", outFile);//写图片
            System.out.println("商户名称:"+entity.name+">>合成图片完成...");
            System.out.println("--------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("商户名称:"+entity.name+">>合成图片失败...");
            System.out.println("--------------------------------------");
        }
    }

    /**
     * 商户实体
     */
    public static class BusinessEntity{
        public String code;
        public String name;
        public String qrcode;
    }

    public static void main(String[] args) {
        File file = new File("./origin");
        if(file == null || !file.exists()){
            System.out.println("!!!!请在当前目录创建origin文件夹,并且放入test.png和test.xls两个文件!!!!!");
            return;
        }
        // 读取Excel表格
        ArrayList<BusinessEntity> list = readExcel("./origin/test.xls");
        createBitmap(list);

//        BusinessEntity businessEntity = new BusinessEntity();
//        businessEntity.code = "123";
//        businessEntity.name = "航空票务代理服务部";
//        waterMark(businessEntity,"./temp/20800321.jpg");
//        ImgYin("我要加的水印ArrayList<BusinessEntity>" , "WechatIMG54.jpeg","test.jpeg");

//        createPicTwo2("test.png", "./temp/a.jpg");
    }

}

