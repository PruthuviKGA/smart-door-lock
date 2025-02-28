const express = require("express");
const mongoose = require("mongoose");
const bodyParser = require("body-parser");
const cors = require("cors");
const dotenv = require("dotenv");
const app = express();
require("dotenv").config();  // to read the ".env" file


const PORT = process.env.PORT || 8070;  //define a port for the server.
app.use(cors());
app.use(bodyParser.json());

const URL = process.env.MONGO_URL;
mongoose.connect(URL, {
   //useCreateIndex: true,
    useNewUrlParser: true,
    useUnifiedTopology: true,
   // useFindAndModify:false,

});

const connection = mongoose.connection;  //connect the database
connection.once("open", () => {
    console.log("Mongodb connection successful");
})

const UserRouter = require("./routes/user.js");
app.use("/user", UserRouter);
const LogRouter = require("./routes/VerificationLog.js");
app.use("/logs", LogRouter);

app.listen(PORT,() =>{
    console.log('Server is running on port '+PORT);
})
