package longbridge.services;


import longbridge.dtos.UserRetrievalDTO;

public interface UserRetrievalService {

    String userNameRetrieval(UserRetrievalDTO userRetrievalDTO);

    String corporateUserNameRetrieval(UserRetrievalDTO userRetrievalDTO);

    String confirmSecurityQuestion(UserRetrievalDTO userRetrievalDTO);

    String sendGeneratedPassword(UserRetrievalDTO userRetrievalDTO);

    String retrievePassword(UserRetrievalDTO userRetrievalDTO);

    String verifyGeneratedPassword(UserRetrievalDTO userRetrievalDTO);

    String validateToken(UserRetrievalDTO userRetrievalDTO);

    String verifyPasswordPolicy(UserRetrievalDTO userRetrievalDTO);

    String sendCorpGeneratedPassword(UserRetrievalDTO userRetrievalDTO);

    String verifyCorpGeneratedPassword(UserRetrievalDTO userRetrievalDTO);

    String verifyCorpPasswordPolicy(UserRetrievalDTO userRetrievalDTO);

    String validateCorpToken(UserRetrievalDTO userRetrievalDTO);

    String getBvn();
}
