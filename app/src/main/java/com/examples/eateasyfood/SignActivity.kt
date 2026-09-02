package com.examples.eateasyfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eateasyfood.R
import com.example.eateasyfood.databinding.ActivitySignBinding
import com.examples.eateasyfood.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Cấu hình đăng nhập Google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Khởi tạo Firebase Auth
        auth = Firebase.auth
        // Khởi tạo tham chiếu đến cơ sở dữ liệu Firebase
        database = Firebase.database.reference
        // Khởi tạo Google
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Bắt sự kiện khi click vào nút "Tạo tài khoản"
        binding.createAccountButton.setOnClickListener {
            // Lấy thông tin từ các trường nhập
            username = binding.userName.text.toString()
            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            // Kiểm tra và yêu cầu điền đầy đủ thông tin
            if(email.isEmpty() || password.isBlank() || username.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }else{
                // Gọi hàm tạo tài khoản
                createAccount(email, password)
            }
        }

        // Bắt sự kiện khi click vào nút "Đã có tài khoản"
        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity:: class.java)
            startActivity(intent)
        }

        // Bắt sự kiện khi click vào nút "Đăng nhập bằng Google"
        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }

    // Đăng ký Activity Result Launcher để xử lý đăng nhập Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account : GoogleSignInAccount ?= task.result
                val credential  = GoogleAuthProvider.getCredential(account ?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Google Sign-In successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity:: class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm tạo tài khoản bằng email và password
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                // Lưu thông tin người dùng vào cơ sở dữ liệu Firebase
                saveUserData()
                startActivity(Intent(this, LoginActivity:: class.java))
                finish()
            }else{
                Toast.makeText(this,"Account creation failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "CreateAccount failed", task.exception)
            }
        }
    }

    // Hàm lưu thông tin người dùng vào cơ sở dữ liệu Firebase
    private fun saveUserData() {
        // Lấy thông tin từ các trường nhập
        username = binding.userName.text.toString()
        email = binding.emailAddress.text.toString()
        password = binding.password.text.toString()

        // Tạo đối tượng UserModel từ thông tin người dùng
        val user = UserModel(username, email,password)
        // Lấy ID của người dùng hiện tại
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        // Lưu thông tin người dùng vào cơ sở dữ liệu Firebase
        database.child("user").child(userId).setValue(user)
    }
}
