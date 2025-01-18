package tw.com.ispan.service.pet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.pet.CasePicture;
import tw.com.ispan.repository.pet.CasePictureRepository;

@Service
@Transactional
public class ImageService {

	@Autowired
	private CasePictureRepository casePictureRepository;

	// 暫存路徑
	@Value("${file.tmp-upload-dir}")
	private String tmpUploadDir;

	// 永存路徑
	@Value("${file.final-upload-dir}")
	private String finalUploadDir;

	// 將圖片暫存於暫存資料夾
	public Map<String, String> tmpSaveImage(MultipartFile file) {

		// 組裝返回訊息(檔名、路徑)
		Map<String, String> fileMessage = new HashMap<>();

		// 在程式啟動時將相對路徑解析為基於專案運行目錄的絕對路徑，System.getProperty("user.dir")可返回當前應用的執行目錄
		Path absolutePath = Paths.get(System.getProperty("user.dir"), tmpUploadDir).toAbsolutePath();

		// 也可使用操作系統的臨時目錄，如果臨時文件只需要短期存儲
		// String tmpDir = System.getProperty("java.io.tmpdir");
		// Path tmpUploadPath = Paths.get(tmpDir, "upload/tmp/pet/images");

		// 生成唯一文件名，防止文件名衝突 (圖片名預計取為memberid_caseid，但須要從token抓會員資料才能抓)
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

		// 如果暫存目錄不存在則先創建目錄
		if (!Files.exists(absolutePath)) {
			try {
				Files.createDirectories(absolutePath);
			} catch (IOException e) {
				System.out.println("路徑錯誤");
				e.printStackTrace();
			}
		}

		// 將文件路徑組合起來
		Path filePath = absolutePath.resolve(fileName);

		// 將上傳的文件內容保存到指定路徑
		try {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("檔案路徑不存在，上傳失敗");
			e.printStackTrace();
		}

		// 最後檢查文件是否順利存儲
		if (!Files.exists(filePath)) {
			System.out.println("文件存儲失敗");
			return null;
		}

		// 上傳成功則返回檔案相關訊息
		fileMessage.put("fileName", fileName);
		fileMessage.put("tempUrl", filePath.toString());
		fileMessage.put("status", "200");
		fileMessage.put("message", "圖片上傳成功，路徑：" + filePath.toString());
		return fileMessage;
	}

	// 將暫存資料夾中圖片移到永存資料夾
	public List<String> moveImages(List<String> tmpUrls) {

		// 用來保存新圖片路徑
		List<String> finalUrl = new ArrayList<String>();

		// step1 先把圖片一個個從暫存資料夾移到永存資料夾，並返回新圖片路徑
		for (String tmpUrl : tmpUrls) {

			// 定義來源檔案路徑
			Path sourcePath = Paths.get(tmpUrl);

			// 定義目標檔案路徑（包括目標檔案名稱）
			// 先從路徑中擷取檔案名稱，再將之與轉為專案執行環境絕對路徑的字串拼接，獲得最終目標檔案路徑
			String fileName = sourcePath.getFileName().toString();
			Path absolutePath = Paths.get(System.getProperty("user.dir"), finalUploadDir).toAbsolutePath();
			Path targetPath = absolutePath.resolve(fileName);

			try {
				// 確保目標目錄存在，否則創建 .getParent()返回檔案目錄而非文件本身
				if (!Files.exists(targetPath.getParent())) {
					Files.createDirectories(targetPath.getParent());
				}
				// 移動檔案
				Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("檔案已成功移動到：" + targetPath);
				finalUrl.add(targetPath.toString());

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("檔案移動失敗：" + e.getMessage());
			}
		}
		System.out.println(finalUrl.toString());
		return finalUrl;
	}

	// 將單個暫存圖片移至永存資料夾
	public String moveImage(String tmpUrl) {

		// 定義來源檔案路徑
		Path sourcePath = Paths.get(tmpUrl);

		// 定義目標檔案路徑（包括目標檔案名稱）
		// 先從路徑中擷取檔案名稱，再將之與轉為專案執行環境絕對路徑的字串拼接，獲得最終目標檔案路徑
		String fileName = sourcePath.getFileName().toString();
		Path absolutePath = Paths.get(System.getProperty("user.dir"), finalUploadDir).toAbsolutePath();
		Path targetPath = absolutePath.resolve(fileName);

		try {
			// 確保目標目錄存在，否則創建 .getParent()返回檔案目錄而非文件本身
			if (!Files.exists(targetPath.getParent())) {
				Files.createDirectories(targetPath.getParent());
			}
			// 移動檔案
			Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("檔案已成功移動到：" + targetPath);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("檔案移動失敗：" + e.getMessage());
		}
		return targetPath.toString();
	}

	// 將圖片路徑保存至資料庫中(casePicture表)
	public List<CasePicture> saveImage(List<String> finalUrls) {

		// 用來返回圖片實體List
		List<CasePicture> casePictures = new ArrayList<>();

		for (String url : finalUrls) {

			CasePicture casePicture = new CasePicture();
			casePicture.setPictureUrl(url);
			CasePicture newCasePicture = casePictureRepository.save(casePicture); // 會返回包含ID的實體
			casePictures.add(newCasePicture);
		}

		return casePictures;
	}

	// 為修改案件圖片時，用於確認傳過來的id+url是否已經存在於圖片資料表中，不存在得都則需轉移到永存資料夾，再修改圖片對應表中資料
	public List<CasePicture> saveModify(Map<Integer, String> ImageIdandUrl) {
		
		//此集合為需要新增的圖片
		List<String> tmpUrls = new ArrayList<String>();
		
		//此集合為要返回的圖片實體
		List<CasePicture> CasePictures = new ArrayList<CasePicture>();
		
		// 遍歷比對新傳來的url集合，url是否存在於圖片表對應圖片id中，已存在的要改，不存在的要新增
		// 使用 keySet() 遍歷所有 key
		for (Integer imageId : ImageIdandUrl.keySet()) {
				Optional<CasePicture> result = casePictureRepository.findById(imageId);
				if(result != null && result.isPresent()) {
					
					//比對此圖片id在原圖片資料表中url和新傳進來的圖片url
					if(result.get().getPictureUrl() != ImageIdandUrl.get(imageId)) {
						
						//如果不相等，就先將這個圖片移到永存資料夾後，拿新的url去修改圖片表中對應圖片id的路徑
						String finalUrl = moveImage(ImageIdandUrl.get(imageId));
			            result.get().setPictureUrl(finalUrl);
			            System.out.println("圖片id為：" + imageId + "的url已改為"+ finalUrl);
			            //將被修改的圖片實體加到要返回的物件中
			            CasePictures.add(result.get());
					} else {
						System.out.println("圖片id：" + imageId + "沒有被修改");
					}
				} else {
					//資料庫找不到對應id，表示為新增圖片，加到等等需要一起被移到永存資料夾並保存於圖片表中的圖片
					tmpUrls.add(ImageIdandUrl.get(imageId));
				}
			}

		// 返回新增圖片實體
	 	List<CasePicture> newCasePictures = saveImage(moveImages(tmpUrls));	
			
	 	//新增圖片實體加入修改圖片實體
	 	newCasePictures.addAll(CasePictures);
			
		System.out.println("圖片新增和修改完成");
		
		return newCasePictures;
		}

}
