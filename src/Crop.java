
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * 이미지 편집 으로만 사용
 * @author ihjang
 *
 */
public class Crop extends JFrame {

	Image image;

	Insets insets;

	BufferedImage img = null;

	public Crop() {
		super();
		try {
			String imgORiginalPath	= "D:/newfolder/node-tree16.png";
			String imgTargetPath= "D:/newfolder/node-tree16.png";    // 새 이미지 파일명
			
			/****************** Crop 사이즈에 맞춰서 자르기 ********************/
//			img = ImageIO.read(new File(imgORiginalPath));
//			File outputfile = new File(imgTargetPath);
//			ImageIO.write(img.getSubimage(150, 540, 110, 110), "png", outputfile);
			/****************** Crop ********************/

			
			
			
			/****************** Crop 사이즈에 맞춰서 줄이기 ********************/
	        String imgFormat 	= "png";// 새 이미지 포맷. jpg, gif 등
	        int newWidth 		= 16;	// 변경 할 넓이
	        int newHeight 		= 16;	// 변경 할 높이
	        String mainPosition = "W";	// W:넓이중심, H:높이중심, X:설정한 수치로(비율무시)
	 
	        Image image;
	        int imageWidth;
	        int imageHeight;
	        double ratio;
	        int w;
	        int h;

			// 원본 이미지 가져오기
			image = ImageIO.read(new File(imgORiginalPath));

			// 원본 이미지 사이즈 가져오기
			imageWidth = image.getWidth(null);
			imageHeight = image.getHeight(null);

			if (mainPosition.equals("W")) { // 넓이기준

				ratio = (double) newWidth / (double) imageWidth;
				w = (int) (imageWidth * ratio);
				h = (int) (imageHeight * ratio);

			} else if (mainPosition.equals("H")) { // 높이기준

				ratio = (double) newHeight / (double) imageHeight;
				w = (int) (imageWidth * ratio);
				h = (int) (imageHeight * ratio);

			} else { // 설정값 (비율무시)

				w = newWidth;
				h = newHeight;
			}

			// 이미지 리사이즈
			// Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
			// Image.SCALE_FAST : 이미지 부드러움보다 속도 우선
			// Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절
			// 알고리즘
			// Image.SCALE_SMOOTH : 속도보다 이미지 부드러움을 우선
			// Image.SCALE_AREA_AVERAGING : 평균 알고리즘 사용
			Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);

			// 새 이미지 저장하기
			BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) newImage.getGraphics();
			int imgW	= newImage.getWidth();
			int imgH	= newImage.getHeight();
			g.drawImage(resizeImage, imgW, 0, 0, imgH, 0, 0, imgW, imgH, null);
			
			g.dispose();
			ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (insets == null) {
			insets = getInsets();
		}
		g.drawImage(image, insets.left, insets.top, this);
	}

	public static void main(String args[]) {
		JFrame f = new Crop();
		f.setSize(200, 200);
		f.show();
	}
}
