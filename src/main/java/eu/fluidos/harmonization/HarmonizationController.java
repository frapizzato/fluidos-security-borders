package eu.fluidos.harmonization;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.fluidos.jaxb.*;

@RestController
public class HarmonizationController {
	private final HarmonizationService HarmonizationService;
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	
	public HarmonizationController(HarmonizationService HarmonizationService) {
        this.HarmonizationService = HarmonizationService;
    }
	
	/* @RequestParam("provider")
	GET(/harmonize)
	Return null if it is not possible to perform the harmonization */
    public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
    	return HarmonizationService.harmonize(provider, consumer);
    }
    
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
