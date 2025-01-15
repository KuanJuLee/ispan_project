package tw.com.ispan.controller.pet;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.ispan.domain.pet.RescueCase;
import tw.com.ispan.dto.pet.RescueCaseDto;
import tw.com.ispan.dto.pet.RescueCaseResponse;
import tw.com.ispan.service.pet.ImageService;
import tw.com.ispan.service.pet.RescueCaseService;

//此controller為會員限定功能
@RestController
@RequestMapping(path = { "/member/RescueCase" })
public class MemberRescueController {

	@Autowired
	private RescueCaseService rescueCaseService;

	@Autowired
	private ImageService imageService;

	// 新增一筆救援案件----------------------------------------------------------------------------------------------------------------------
	@PostMapping(path = { "/add" })
	public RescueCaseResponse add(@RequestHeader("Authorization") String token,
			@RequestPart ("rescueCaseDto") RescueCaseDto rescueCaseDto,
			@RequestPart (name = "files") MultipartFile[] files) {

		System.out.println("近來優");
		
		// 方法參數: 1. 專案使用JWT(JSON Web Token)來管理會員登入，則可以從前端傳入的 JWT 中提取重要資訊
		// 2. rescueCaseDto傳進service存資料，而RescueCaseResponse回傳給前端
		// 3. MultipartFile接收圖片
		// 4. 由於要同時接收圖檔、也要接收json文字，因此用@RequestPart來接收request中文字資料(rescueCaseDto)
		RescueCaseResponse response = new RescueCaseResponse();

		// 傳進來的資料需要驗證(前端即時驗證一次，後端驗證一次)
		// 1.驗證token

		// 2.驗證必填資料都要存在(沒寫傳進來dto接收會是預設初始值)
		if (rescueCaseDto.getCaseTitle() == null | rescueCaseDto.getSpeciesId() == null
				| rescueCaseDto.getSuspLost() == null | rescueCaseDto.getCityId() == null
				| rescueCaseDto.getDistinctAreaId() == null | rescueCaseDto.getRescueReason() == null
				| rescueCaseDto.getCasePictures() == null | rescueCaseDto.getRescueDemands() == null
				| rescueCaseDto.getCanAffords() == null) {
			response.setSuccess(false);	
			response.setMessage("請填入必填資料");
			return response;
		}
		
		if (rescueCaseDto.getCaseTitle() == null | rescueCaseDto.getSpeciesId() == null
				| rescueCaseDto.getSuspLost() == null | rescueCaseDto.getCityId() == null
				| rescueCaseDto.getDistinctAreaId() == null | rescueCaseDto.getRescueReason() == null
				| rescueCaseDto.getCasePictures() == null | rescueCaseDto.getRescueDemands() == null
				| rescueCaseDto.getCanAffords() == null) {
			response.setSuccess(false);	
			response.setMessage("請填入必填資料");
			return response;
		}

		// 先convertToEntity()轉為實體類別後，add()把該存的放進去(經緯度等..)再存入資料庫中
		RescueCase rescueCaseEntity = rescueCaseService.convertToEntity(rescueCaseDto);
		RescueCase rescueCase = rescueCaseService.add(rescueCaseEntity, token);
		
		// 圖片存入本地+資料庫中
		try {
			if (files != null) {
				imageService.saveImage(files);
			} else {
				response.setSuccess(false);
				response.setMessage("請上傳圖片檔");
				return response;
			}
		} catch (IOException e) {
			System.out.println("圖片儲存失敗");
			e.printStackTrace();
			return null;
		}

		if (rescueCase != null) {
			// 新增成功
			response.setSuccess(true);
			response.setMessage("新增案件成功");
			return response;
		} else {
			// 新增失敗，如果rescueCase == null
			response.setSuccess(false);
			response.setMessage("新增案件失敗");
			return response;
		}
	}

	// 修改救援案件-----------------------------------------------------------------------------------------------------------------------------
	@PutMapping(path = { "/modify/{id}" })
	public RescueCaseResponse modifiedRescueCase(@PathVariable(name = "id") Integer id,
			@RequestHeader("Authorization") String token, @RequestBody RescueCaseDto rescueCaseDto,
			@RequestParam(name = "files") MultipartFile[] files) {

		// 除了原本新增案件的內容都可修改外，重點是多一個可修改caseState，因此和新增案件不同點在於這裡dto內的caseState會有資料而非null
		// 案件id要從前端點選修改按鈕(按鈕做成超連結)時同時送出，因此id即藏在超連結送出的request line裡

		RescueCaseResponse response = new RescueCaseResponse();

		// 傳進來的資料需要驗證(前端即時驗證一次，後端驗證一次)
		// 1.驗證token

		// 2.驗證必填資料都要存在(沒寫傳進來會是預設初始值)
		if (rescueCaseDto.getCaseTitle() == null | rescueCaseDto.getSpeciesId() == null
				| rescueCaseDto.getBreedId() == null | rescueCaseDto.getSuspLost() == null
				| rescueCaseDto.getCityId() == null | rescueCaseDto.getDistinctAreaId() == null
				| rescueCaseDto.getRescueReason() == null | rescueCaseDto.getCasePictures() == null
				| rescueCaseDto.getRescueDemands() == null | rescueCaseDto.getCanAffords() == null) {
			response.setSuccess(false);
			response.setMessage("請填入必填資料");
			return response;
		}

		// 3. 驗證此id是否存在於資料表中，有存在才繼續往service丟
		if (id == null) {
			response.setSuccess(false);
			response.setMessage("必須給予案件id");
			return response;
		} else if (!rescueCaseService.exists(id)) {
			response.setSuccess(false);
			response.setMessage("id不存在於資料中");
			return response;
		}
		
		
		// 圖片存入本地+資料庫中
				try {
					if (files != null) {
						imageService.saveImage(files);
					} else {
						response.setSuccess(false);
						response.setMessage("請上傳圖片檔");
						return response;
					}
				} catch (IOException e) {
					System.out.println("圖片儲存失敗");
					e.printStackTrace();
					return null;
				}
		

		// 若id存在，就去修改這筆資料
		RescueCase rescueCaseEntity = rescueCaseService.convertToEntity(rescueCaseDto);
		RescueCase rescueCase = rescueCaseService.modify(rescueCaseEntity, id);
		if (rescueCase != null) {
			// 修改成功
			response.setSuccess(true);
			response.setMessage("修改案件成功");
			return response;
		} else {
			// 修改失敗，回傳rescueCase == null
			response.setSuccess(false);
			response.setMessage("修改案件失敗");
			return response;
		}
	}

	// 刪除救援案件-----------------------------------------------------------------------------------------------------------------------------

}
