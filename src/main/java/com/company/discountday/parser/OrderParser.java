package com.company.discountday.parser;

import com.company.discountday.model.Order;

public interface OrderParser {

    Order parse(String line);
}