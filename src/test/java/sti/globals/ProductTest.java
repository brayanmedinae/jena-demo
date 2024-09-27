package sti.globals;

import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductTest.class);

    @Test
    void discountTest() {
        double start = System.currentTimeMillis();

        // Create a model
        Model model = ModelFactory.createDefaultModel();
        String ns = "http://example.org/products#";

        // Create properties
        Property hasCategory = model.createProperty(ns + "hasCategory");
        Property hasDiscount = model.createProperty(ns + "hasDiscount");

        // Create product list
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "premium"));
        products.add(new Product(2, "standard"));
        products.add(new Product(3, "normal"));

        // Add products to the model
        for (Product product : products) {
            model.createResource(ns + product.getId())
                    .addProperty(hasCategory, product.getCategory());
        }

        // Define the rules
        String rules =
                "[premiumRule: (?product " + hasCategory + " 'premium') -> (?product " + hasDiscount + " '20%')]" +
                        "[standardRule: (?product " + hasCategory + " 'standard') -> (?product " + hasDiscount + " '5%')]" +
                        "[normalRule: (?product " + hasCategory + " 'normal') -> (?product " + hasDiscount + " '0%')]";

        // Create a reasoner with the rules
        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        // Query the model and update product objects
        for (Product product : products) {
            Resource productResource = infModel.getResource(ns + product.getId());
            Statement discountStmt = productResource.getProperty(hasDiscount);
            if (discountStmt != null) {
                product.setDiscount(discountStmt.getString());
            }
        }

        // Print results
        for (Product product : products) {
            LOGGER.info(product.toString());
        }

        assertEquals("20%", products.get(0).getDiscount());
        assertEquals("5%", products.get(1).getDiscount());
        assertEquals("0%", products.get(2).getDiscount());

        double end = System.currentTimeMillis();
        LOGGER.info("Execution time: {} ms", (end - start));
    }
}
