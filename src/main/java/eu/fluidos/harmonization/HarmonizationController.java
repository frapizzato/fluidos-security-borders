package eu.fluidos.harmonization;



import java.util.List;

import eu.fluidos.jaxb.*;

//REST API Interface missing
public class HarmonizationController {

	private final HarmonizationService HarmonizationService;

	public HarmonizationController(HarmonizationService harmonizationService) {
        this.HarmonizationService = harmonizationService;
    }
	
	//@RequestParam("provider")
	//GET(/harmonize)
    public List<ConfigurationRule> harmonize(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
    	return HarmonizationService.harmonize(provider, consumer);
    }
    
    public boolean verify(ITResourceOrchestrationType provider, ITResourceOrchestrationType consumer) {
    	return HarmonizationService.verify(provider, consumer);
    }
}
