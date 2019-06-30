package com.leilaodequadrinhos.api.model.task.auction;

import com.leilaodequadrinhos.api.model.dao.AuctionDao;
import com.leilaodequadrinhos.api.model.dao.AuctionStatusDao;
import com.leilaodequadrinhos.api.model.dao.BidDao;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.AuctionStatusDAO;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.AuctionDAO;
import com.leilaodequadrinhos.api.model.dao.impl.jdbc.BidDAO;
import com.leilaodequadrinhos.api.model.entities.Auction;
import com.leilaodequadrinhos.api.model.entities.AuctionStatus;
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
        AuctionStatusDao auctionStatusDao = new AuctionStatusDAO();
        AuctionStatus auctionStatus = auctionStatusDao.findById(auctionStatusID);
        BidDao bidDao = new BidDAO();
        Long bidCount = bidDao.BidCount(auctionID);

        if(bidCount == 0){
            boolean changeStatusToActive = auctionStatusID == 1;
            boolean changeStateToInactive = auctionStatusID == 2;
            boolean changeStateToOnHold = auctionStatusID == 3;
            boolean changeStateToFinalized = auctionStatusID == 4;
            boolean changeStateToCanceled = auctionStatusID == 5;

            switch (auction.getAuctionStatus().getStatus()){
                case "ATIVO":
                    if (changeStateToInactive || changeStateToOnHold || changeStateToCanceled || changeStateToFinalized){
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