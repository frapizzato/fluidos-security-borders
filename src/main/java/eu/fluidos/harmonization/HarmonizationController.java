package eu.fluidos.harmonization;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import eu.fluidos.jaxb.*;

@RestController
@RequestMapping("/api/harmonization")
public class HarmonizationController {
	private final HarmonizationService HarmonizationService;
	private ITResourceOrchestrationType providerIntents, consumerIntents;

	public HarmonizationController(HarmonizationService HarmonizationService) {
		this.HarmonizationService = HarmonizationService;
	}

	/*
	 * @RequestParam("provider") GET(/harmonize) Return null if it is not possible
	 * to perform the harmonization
	 */
	@GetMapping("/harmonize")
	@ResponseStatus(HttpStatus.OK)
	public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider,
			ITResourceOrchestrationType consumer) {
		return HarmonizationService.harmonize(provider, consumer);
	}

	@GetMapping("/verify")
	@ResponseStatus(HttpStatus.OK)
	public boolean verify(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
		return HarmonizationService.verify(provider, consumer);
	}
    
    /* Temporary */ 
    
    public ITResourceOrchestrationType getProviderIntents() {
		return HarmonizationService.getProviderIntents();
	}

	public void setProviderIntents(ITResourceOrchestrationType intents) {
		HarmonizationService.setProviderIntents(intents);
	}
	
	public ITResourceOrchestrationType getConsumerIntents() {
		return HarmonizationService.getConsumerIntents();
	}

	public void setConsumerIntents(ITResourceOrchestrationType consumerIntents) {
		HarmonizationService.setConsumerIntents(consumerIntents);
	}
}
