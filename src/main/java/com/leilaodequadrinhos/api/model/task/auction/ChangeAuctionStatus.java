package com.leilaodequadrinhos.api.model.task.auction;

import com.leilaodequadrinhos.api.model.dao.AuctionDao;
import com.leilaodequadrinhos.api.model.dao.AuctionStatusDao;
import com.leilaodequadrinhos.api.model.dao.BidDao;
import com.leilaodequadrinhos.api.model.dao.ProductDao;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.AuctionStatusDAO;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.AuctionDAO;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.BidDAO;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.ProductDAO;
import com.leilaodequadrinhos.api.model.entities.Auction;
import com.leilaodequadrinhos.api.model.entities.AuctionStatus;
import com.leilaodequadrinhos.api.model.entities.Product;
import com.leilaodequadrinhos.api.model.entities.ProductStatus;
import com.leilaodequadrinhos.api.model.task.Task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangeAuctionStatus implements Task {

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        Long auctionID = Long.parseLong(request.getParameter("auctionID"));
        Long auctionStatusID = Long.parseLong(request.getParameter("auctionStatusID"));
        AuctionDao auctionDao = new AuctionDAO();
        Auction auction = (Auction) auctionDao.findById(auctionID);
        AuctionStatus auctionStatus = auction.getAuctionStatus();
        ProductDao productDao = new ProductDAO();
        Product product = auction.getProduct();
        ProductStatus productStatus = product.getProductStatus();

        BidDao bidDao = new BidDAO();
        Boolean BIDZERO = bidDao.BidCount(auctionID)==0;
        int duration = (int)auction.getDuration();

        boolean productActive = productStatus.getProductStatusID() ==1;
        boolean productInActive = productStatus.getProductStatusID() ==2;
        boolean productInAuction = productStatus.getProductStatusID() ==3;
        boolean productAuctioned = productStatus.getProductStatusID() ==4;

        boolean changeStatusToActive = auctionStatusID == 1;
        boolean changeStateToInactive = auctionStatusID == 2;
        boolean changeStateToOnHold = auctionStatusID == 3;
        boolean changeStateToConcluded = auctionStatusID == 4;
        boolean changeStateToCanceled = auctionStatusID == 5;

        if (auction.getAuctionStatus().getStatus().equalsIgnoreCase("ATIVO")){
            if(changeStateToConcluded && duration==0){
                auctionDao.changesAuctionStatus(auctionID, auctionStatus);
                productStatus.setProductStatusID(4);
                productDao.changeStatusProduct((long) product.getProductID(),productStatus);
                return "Auction status changed to CONCLUDED";
            } else {
                return "The auction duration has not yet spired";
            }
        }

        if(BIDZERO){
            switch (auction.getAuctionStatus().getStatus()){
                case "ATIVO":
                    if (changeStateToInactive || changeStateToOnHold || changeStateToCanceled){
                    auctionDao.changesAuctionStatus(auctionID, auctionStatus);
                    return "Auction status changed";
                } else {
                    return "Cannot change the Auction status";
                }
                case "INATIVO": if (changeStatusToActive || changeStateToOnHold || changeStateToCanceled){
                    auctionDao.changesAuctionStatus(auctionID, auctionStatus);
                        if(auctionStatusID == 1){auctionDao.setAuctionDateNow(auctionID);}
                    return "Auction status changed";
                } else {
                    return "Cannot change the Auction status";
                }
                case "EM_ESPERA": if (changeStatusToActive || changeStateToInactive || changeStateToCanceled){
                    auctionDao.changesAuctionStatus(auctionID, auctionStatus);
                        if(auctionStatusID == 1){auctionDao.setAuctionDateNow(auctionID);}
                    return "Auction status changed";
                } else {
                    return "Cannot change the Auction status";
                }
                case "CONCLUIDO":
                case "CANCELADO":
                    return "Cannot change the Auction status";
            }
        } else{
            return "Cannot change an Auction status that have bids";
        }
        auctionDao.changesAuctionStatus(auctionID, auctionStatus);
        return "Auction status changed";
    }
}