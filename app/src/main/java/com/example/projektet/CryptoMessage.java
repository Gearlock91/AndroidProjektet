package com.example.projektet;

public class CryptoMessage {

    // Message.java
        private String text; // message body
        private String memberNickname;
        private boolean belongsToCurrentUser; // is this message sent by us?

        public CryptoMessage(String text, boolean belongsToCurrentUser){
            this(text,null,belongsToCurrentUser);
        }


        public CryptoMessage(String text, String memberNickname, boolean belongsToCurrentUser) {
            this.text = text;
            this.memberNickname = memberNickname;
            this.belongsToCurrentUser = belongsToCurrentUser;
        }

        public String getText() {
            return text;
        }

        public String getMemberNickname() {
            return memberNickname;
        }

        public boolean isBelongsToCurrentUser() {
            return belongsToCurrentUser;
        }


}
