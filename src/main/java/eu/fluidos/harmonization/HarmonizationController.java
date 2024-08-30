package eu.fluidos.harmonization;

import eu.fluidos.jaxb.AuthorizationIntents;
import eu.fluidos.jaxb.ConfigurationRule;
import eu.fluidos.jaxb.ITResourceOrchestrationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/harmonization")
public class HarmonizationController {
	private final HarmonizationService harmonizationService;
	private ITResourceOrchestrationType providerIntents, consumerIntents;


	@Autowired
	public HarmonizationController(HarmonizationService HarmonizationService) {
		this.harmonizationService = HarmonizationService;
	}

	/*
	 * @RequestParam("provider") GET(/harmonize) Return null if it is not possible
	 * to perform the harmonization
	 */
	@GetMapping("/harmonize")
	@ResponseStatus(HttpStatus.OK)
	public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider,
			ITResourceOrchestrationType consumer) {
		return harmonizationService.harmonize(provider, consumer);
	}

	@GetMapping("/verify")
	@ResponseStatus(HttpStatus.OK)
	public boolean verify(AuthorizationIntents authIntentsProvider) {
		return harmonizationService.verify(authIntentsProvider);
	}
    
    /* Temporary */ 
    
    public ITResourceOrchestrationType getProviderIntents() {
		return harmonizationService.getProviderIntents();
	}

	public void setProviderIntents(ITResourceOrchestrationType intents) {
		harmonizationService.setProviderIntents(intents);
	}
	
	public ITResourceOrchestrationType getConsumerIntents() {
		return harmonizationService.getConsumerIntents();
	}

	public void setConsumerIntents(ITResourceOrchestrationType consumerIntents) {
		harmonizationService.setConsumerIntents(consumerIntents);
	}
}
