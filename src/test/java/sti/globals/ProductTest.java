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

import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductTest.class);

    @Test
    void discountTest() {
        double start = System.currentTimeMillis();

        Model model = ModelFactory.createDefaultModel();
        String ns = "http://example.org/products#";

        Property hasCategory = model.createProperty(ns + "hasCategory");
        Property hasDiscount = model.createProperty(ns + "hasDiscount");

        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "premium", 100));
        products.add(new Product(2, "standard", 100));
        products.add(new Product(3, "normal", 100));

        for (Product product : products) {
            model.createResource(ns + product.getId())
                    .addProperty(hasCategory, product.getCategory());
        }

        String rules =
                "[premiumRule: (?product " + hasCategory + " 'premium') -> (?product " + hasDiscount + " 0.20)]" +
                        "[standardRule: (?product " + hasCategory + " 'standard') -> (?product " + hasDiscount + " 0.05)]" +
                        "[normalRule: (?product " + hasCategory + " 'normal') -> (?product " + hasDiscount + " 0)]";

        Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        for (Product product : products) {
            Resource productResource = infModel.getResource(ns + product.getId());
            Statement discountStmt = productResource.getProperty(hasDiscount);
            if (discountStmt != null) {
                product.setDiscount(product.getPrice() * discountStmt.getDouble());
            }
        }

        for (Product product : products) {
            LOGGER.info(product.toString());
        }

        assertEquals(80, round(products.get(0).getPrice() - products.get(0).getDiscount()));
        assertEquals(95, round(products.get(1).getPrice() - products.get(1).getDiscount()));
        assertEquals(100, round(products.get(2).getPrice() - products.get(2).getDiscount()));

        double end = System.currentTimeMillis();
        LOGGER.info("Execution time: {} ms", (end - start));
    }
}
