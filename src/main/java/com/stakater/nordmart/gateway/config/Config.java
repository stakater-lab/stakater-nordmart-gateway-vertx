package com.stakater.nordmart.gateway.config;

public class Config
{
    private Address catalogAddress;
    private Address inventoryAddress;
    private Address cartAddress;
    private Address reviewAddress;
    private Address productSearchAddress;
    private Integer serverPort;
    private Boolean disableCartDiscovery;

    public static Config getConfig()
    {
        Config config = new Config();
        config.cartAddress = new Address(System.getenv("CART_API_HOST"), System.getenv("CART_API_PORT"));

        config.catalogAddress = new Address(System.getenv("CATALOG_API_HOST"),
            System.getenv("CATALOG_API_PORT"));

        config.inventoryAddress = new Address(System.getenv("INVENTORY_API_HOST"),
            System.getenv("INVENTORY_API_PORT"));

        config.reviewAddress = new Address(System.getenv("REVIEW_API_HOST"),
                System.getenv("REVIEW_API_PORT"));

        config.productSearchAddress = new Address(System.getenv("PRODUCT_SEARCH_API_HOST"),
                System.getenv("PRODUCT_SEARCH_API_PORT"));

        try
        {
            config.serverPort = Integer.parseInt(System.getenv("HTTP_PORT"));
        } catch (NumberFormatException ignored)
        {
            config.serverPort = 8080;
        }

        config.disableCartDiscovery = Boolean.parseBoolean(System.getenv("DISABLE_CART_DISCOVERY"));

        return config;
    }

    public Address getCatalogAddress()
    {
        return catalogAddress;
    }

    public Address getInventoryAddress()
    {
        return inventoryAddress;
    }

    public Address getCartAddress()
    {
        return cartAddress;
    }

    public Address getReviewAddress() { return reviewAddress; }

    public Integer getServerPort()
    {
        return serverPort;
    }

    public Boolean isDisableCartDiscovery()
    {
        return disableCartDiscovery;
    }

    public Address getProductSearchAddress() {
        return productSearchAddress;
    }
}
