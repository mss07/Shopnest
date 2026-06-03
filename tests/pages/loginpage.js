class LoginPage {
    constructor(page){
        this.page = page;
        
        // Navigation / Trigger Buttons
        this.LoginButton = page.getByRole('button', { name: 'Login' });
        this.createAccountButton = page.getByText('New to flipkart? create');
        
        // Sign-Up Input Fields
        this.FirstNameTextBox = page.getByRole('textbox', { name: 'Enter first name' });
        this.LastNameTextBox = page.getByRole('textbox', { name: 'Enter last name' });
        this.SignUpUsernameTextBox = page.getByRole('textbox', { name: 'Enter username' });
        this.EmailTextBox = page.getByRole('textbox', { name: 'Enter Email' });
        this.SignUpPasswordTextBox = page.getByRole('textbox', { name: 'Enter password' });
        this.PhoneTextBox = page.getByRole('textbox', { name: 'Enter phone' });
        this.ContinueButton = page.locator("//button[normalize-space()='Continue']");

        // Login Input Fields
        this.LoginUsernameTextBox = page.getByRole('textbox', { name: 'Enter username' });
        this.LoginPasswordTextBox = page.getByRole('textbox', { name: 'password' });
        this.SubmitLoginButton = page.locator("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-text MuiButton-textPrimary MuiButton-sizeMedium MuiButton-textSizeMedium MuiButton-colorPrimary MuiButton-root MuiButton-text MuiButton-textPrimary MuiButton-sizeMedium MuiButton-textSizeMedium MuiButton-colorPrimary css-1bjqxys-MuiButtonBase-root-MuiButton-root']");
    }

    async navigate() {
        await this.page.goto('http://localhost:3000/');
    }

    async openLoginModal() {
        await this.LoginButton.first().click();
    }

    async openSignUpModal() {
        await this.openLoginModal();
        await this.createAccountButton.click();
    }

    async login(username, password) {
        await this.openLoginModal();
        await this.LoginUsernameTextBox.fill(username);
        await this.LoginPasswordTextBox.fill(password);
        await this.SubmitLoginButton.click();
    }

    async signup(user) {
        await this.openSignUpModal();
        await this.FirstNameTextBox.fill(user.firstName);
        await this.LastNameTextBox.fill(user.lastName);
        await this.SignUpUsernameTextBox.fill(user.username);
        await this.EmailTextBox.fill(user.email);
        await this.SignUpPasswordTextBox.fill(user.password);
        await this.PhoneTextBox.fill(user.phone);
        await this.ContinueButton.click();
    }

    userProfile(name) {
        return this.page.locator(`//p[normalize-space()='${name}']`);
    }
}

export default LoginPage;