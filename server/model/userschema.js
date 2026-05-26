import mongoose from "mongoose";

const Userschema = new mongoose.Schema({

  firstname:{

    type: String,
    // required: true,
    trim: true,
    minLength: 3,
    maxLength: 20

  },
  lastname:{

    type: String,
    // required: true,
    trim: true,
    minLength: 3,
    maxLength: 20

  },
  username:{

    type: String,
    // required: true,
    trim: true,
    unique:true,
    index: true,
    lowercase:true

  },
  email:{

    type: String,
    // required: true,
    trim: true,
    unique:true,
    match: [/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/, 'Please fill a valid email address']

  },

  password:{

    type: String,
    // required: true

  },
  phone:{

    type: String,
    required: true
   
  }
   



});

const User = mongoose.model('User', Userschema);

export default User;