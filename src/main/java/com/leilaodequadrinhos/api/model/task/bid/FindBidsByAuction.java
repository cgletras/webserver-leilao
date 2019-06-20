package com.leilaodequadrinhos.api.model.task.bid;

import com.leilaodequadrinhos.api.model.dao.LanceDao;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.LanceDAO;
import com.leilaodequadrinhos.api.model.entities.Lance;
import com.leilaodequadrinhos.api.model.task.Task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class FindBidsByAuction implements Task {

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LanceDao lanceDao = new LanceDAO();
        List<Lance> lances = lanceDao.findBidLeilao(Long.parseLong(request.getParameter("auctionID")));
        request.setAttribute("lances", lances);
        return lances;
    }
}
