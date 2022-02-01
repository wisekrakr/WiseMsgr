"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database
  .ref("notifications/{from}/{to}")
  .onWrite((data, context) => {
    console.log(context);

    const sender = context.params.from;
    const receiver = context.params.to;

    if (!data.after.val()) {
      console.log("Notification removed");
      return null;
    }

    const deviceToken = admin
      .database()
      .ref(`/users/${receiver}/deviceToken`)
      .once("value");

    return deviceToken.then((result) => {
      const tokenId = result.val();

      const payload = {
        notification: {
          title: "New chat request",
          body: "You received a new chat request. Click to handle it.",
          icon: "default",
        },
      };

      return admin
        .messaging()
        .sendToDevice(tokenId, payload)
        .then((response) => {
          console.log(response);
        });
    });
  });
