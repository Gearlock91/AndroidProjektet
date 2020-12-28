package com.example.projektet;

public class CryptoMessage {

    // Message.java
        private String text; // message body
        private MemberData memberData; // data of the user that sent this message
        private boolean belongsToCurrentUser; // is this message sent by us?

        public CryptoMessage(String text){
            this(text,null,false);
        }

        public CryptoMessage(String text, MemberData memberData, boolean belongsToCurrentUser) {
            this.text = text;
            this.memberData = memberData;
            this.belongsToCurrentUser = belongsToCurrentUser;
        }

        public String getText() {
            return text;
        }

        public MemberData getMemberData() {
            return memberData;
        }

        public boolean isBelongsToCurrentUser() {
            return belongsToCurrentUser;
        }


}
