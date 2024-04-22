package eu.fluidos.harmonization;



import java.util.List;

import eu.fluidos.jaxb.*;

//REST API Interface missing
public class HarmonizationController {
	private ITResourceOrchestrationType providerIntents, consumerIntents;
	
	private final HarmonizationService HarmonizationService;

	public HarmonizationController() {
        this.HarmonizationService = new HarmonizationService();
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
