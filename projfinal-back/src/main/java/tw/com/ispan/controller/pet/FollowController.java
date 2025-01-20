package tw.com.ispan.controller.pet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.dto.pet.RescueCaseResponse;
import tw.com.ispan.jwt.JsonWebTokenUtility;
import tw.com.ispan.repository.admin.MemberRepository;
import tw.com.ispan.repository.pet.FollowRepository;
import tw.com.ispan.repository.pet.RescueCaseRepository;
import tw.com.ispan.service.pet.CaseService;
import tw.com.ispan.service.pet.RescueCaseService;

//此為會員追蹤某案件
@RestController
@RequestMapping(path = { "/Case/follow" })
public class FollowController {

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RescueCaseRepository rescueCaseRepository;
	
	@Autowired
	JsonWebTokenUtility jsonWebTokenUtility;
	
	@Autowired
	private RescueCaseService rescueCaseService;
	
	@Autowired
	private CaseService caseService;

	// 會員增加追蹤某案件
	// 當按下follow要能區分使用者是對rescue, lost還是adoption case執行追蹤，因為相同caseId在這三表中都有，要查對表。caseType會藏在前端的
	@PutMapping("/add")
    public RescueCaseResponse followCase(@RequestHeader("Authorization") String token,  @RequestAttribute("memberId") Integer memberId, @RequestParam Integer caseId,  @RequestParam String caseType) {
		
		//此response其實不限至於rescueCase使用，但為我自己開發時創立的，為避免和其他人設置的衝突所以取狹義名稱
		RescueCaseResponse response = new RescueCaseResponse();
		
		// 1.驗證token(能通過JsonWebTokenInterceptor攔截器即驗證)並拿到會員id，需驗證此id有無在會員資料表中存在
		System.out.println("此為會員id" + memberId + "執行案件追蹤");
		if (memberId == null) {
			response.setSuccess(false);
			response.setMessage("必須給予會員id");
			return response;
		} else if (!memberRepository.existsById(memberId)) {
			response.setSuccess(false);
			response.setMessage("此會員id不存在於資料中");
			return response;
		}
		
		
		// 2. 驗證前端有傳遞caseId和一併傳遞案件類型過來，且此caseType案件表中此id案件存在
		if (caseId == null || caseType == null || caseType.isEmpty()) {
			response.setSuccess(false);
			response.setMessage("必須給予案件id和案件類型");
			return response;
		} else if (!caseService.caseExists(caseId, caseType)) {
            response.setSuccess(false);
            response.setMessage( caseType + "表中不存在id為" + caseId + "的案件");
            return response;
        }
		
		
		// 3. 防重複追蹤： 在新增之前，應該檢查是否該會員已經追蹤過此案件
		
		
		
		//4. 確認沒追蹤過後，增添follow表資料
		caseService.addFollow(memberId,caseId,caseType);
		response.setSuccess(true);
        response.setMessage("會員id"+memberId+"的會員成功追蹤" + caseType + caseId + "案件");
        return response;
		
	}
}
