package eu.fluidos.harmonization;



import java.util.List;

import eu.fluidos.jaxb.*;

//REST API Interface missing
public class HarmonizationController {

	private final HarmonizationService HarmonizationService;

	public HarmonizationController(HarmonizationService harmonizationService) {
        this.HarmonizationService = harmonizationService;
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
    
    
}
