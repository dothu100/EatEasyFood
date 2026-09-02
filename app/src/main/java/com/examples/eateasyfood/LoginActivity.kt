package com.examples.eateasyfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eateasyfood.R
import com.example.eateasyfood.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
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

        // Khởi tạo Google SignInClient
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // Bắt sự kiện khi click vào nút đăng nhập
        binding.loginButton.setOnClickListener {
            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            // Kiểm tra và yêu cầu điền đầy đủ thông tin email và password
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                signInWithEmailAndPassword(email, password)
            }
        }

        // Bắt sự kiện khi click vào nút đăng nhập bằng Google
        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        // Bắt sự kiện khi click vào nút "Chưa có tài khoản"
        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
    }

    // Đăng ký Activity Result Launcher để xử lý đăng nhập Google
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm đăng nhập bằng email và password
    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
//                    createUserWithEmailAndPassword(email, password)
                    Toast.makeText(this, "Account does not exist!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Hàm onStart kiểm tra xem người dùng đã đăng nhập trước đó chưa
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser!= null) {
            // Nếu đã đăng nhập, chuyển đến MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    // Hàm cập nhật giao diện người dùng sau khi đăng nhập thành công
    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(this, "Sign-In successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
