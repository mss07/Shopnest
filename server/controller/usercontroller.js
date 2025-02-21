import User from "../model/userschema.js";

export const usersignup = async (request, response) => {
  try {
    // console.log(request.body.phone);
    const exist = await User.findOne({ username: request.body.username });
    if (exist) {
        return response.status(401).json({ message: "username already exist" });
    } else {
        console.log(request.body.phone);
      const user = request.body;
      const newuser = new User(user);
      await newuser.save();

      response.status(200).json({ message: user });
    }
  } catch (error) {
    response.status(500).json({ message: error.message });
  }
};

export const userlogin = async(request, response)=>{

  try {
          const username= request.body.username;
          const password = request.body.password;

          let user = await User.findOne({username: username, password: password});
          if(user){

           return response.status(200).json({ data: user});


          }

          else{
           return response.status(401).json('invalid login');

          }
  } catch (error) {
    response.status(500).json('error', error.message);
  }
  





}

export default usersignup;
