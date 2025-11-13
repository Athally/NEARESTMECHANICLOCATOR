const functions=require ("firebase-functions");
const admin=require("firebase-admin");
const sgMail=require(@sendgrid/mail);

admin.initializeApp();
const SENDGRID_API_KEY="";
sgMail.setApiKey("SENDGRID_API_KEY");
exports.sendWelcomeEmail=functions.database
   .ref("/{userType}/{userId}")
   .onCreate((snapshot,context)=>{const userType=context.params.userType;//"Drivers" or "Mechanics"
   const userData=snapshot.val();
   const{name,email}=userData;
   const msg={
   to:email,
   from:"nearestmechaniclocator@gmail.com"
    subject:Welcome to Nearest Mechanic Locator,${name}!
    text:Hi ${name},n/nThank you for registering as a ${userType.slice(0,-1)}.We are excited to have you join us!,
    html:<strong>Hi${name},</strong><br><br>Thank you for registering as a ${userType.slice(0,-1)}.We are excited to have you join us!,
   };
   return sgMail
   .send(msg)
   .then(()=>console.log("Email sent to:",email))
   .catch((error)=>console.error("Error sending email:",error))
   });
