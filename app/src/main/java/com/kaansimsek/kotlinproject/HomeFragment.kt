package com.kaansimsek.kotlinproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.toUpperCase
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment() {

    private lateinit var containerLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val button = view.findViewById<Button>(R.id.btnNavigateToActivityAd)
        containerLayout = view.findViewById(R.id.containerLayout)

        button.setOnClickListener {
            val intent = Intent(requireContext(), AdActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firestore'dan verileri al ve arayüzü güncelle
        fetchDataAndPopulateUI()
    }

    private fun fetchDataAndPopulateUI() {
        val db = FirebaseFirestore.getInstance()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = db.collection("animals").get().await()
                for (document in querySnapshot.documents) {
                    val animalName = document.getString("name")
                    val animalLocation = document.getString("location")
                    val animalAge = document.getLong("age")
                    val photoUrl = document.getString("photoUrl")
                    val ad_id = document.id
                    val cardview_id = ad_id.hashCode()
                    val string_cardview_id = cardview_id.toString()

                    // CardView oluştur
                    val cardView = CardView(requireContext())
                    val layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, 16)
                    cardView.layoutParams = layoutParams
                    cardView.radius = 8f
                    cardView.cardElevation = 8f

                    // Horizontal LinearLayout oluştur
                    val horizontalLayout = LinearLayout(requireContext())
                    horizontalLayout.orientation = LinearLayout.HORIZONTAL
                    cardView.addView(horizontalLayout)

                    // ImageView oluştur ve ayarla
                    val imageView = ImageView(requireContext())
                    val imageLayoutParams = LinearLayout.LayoutParams(
                        300,
                        300,
                    )
                    imageLayoutParams.setMargins(64, 64, 64, 64) // Sağdan, soldan, yukarıdan, aşağıdan padding ayarlayın
                    imageView.layoutParams = imageLayoutParams
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(requireContext())
                        .load(photoUrl)
                        .into(imageView)
                    horizontalLayout.addView(imageView)

// Dikey LinearLayout oluştur
                    val textInfoLayout = LinearLayout(requireContext())
                    textInfoLayout.orientation = LinearLayout.VERTICAL
                    textInfoLayout.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    textInfoLayout.gravity = Gravity.CENTER_VERTICAL // Dikey olarak ortalamak için gravity ekleyin
                    horizontalLayout.addView(textInfoLayout)

                    val nameTextView = TextView(requireContext())
                    nameTextView.text = "${animalName?.toUpperCase()}"
                    nameTextView.textSize = 28f // Yazı boyutunu ayarlayın
                    nameTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    nameTextView.gravity = Gravity.CENTER // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(nameTextView)

                    val ageTextView = TextView(requireContext())
                    ageTextView.text = "$animalAge years old"
                    ageTextView.textSize = 24f // Yazı boyutunu ayarlayın
                    ageTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    ageTextView.gravity = Gravity.CENTER // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(ageTextView)

                    val locationTextView = TextView(requireContext())
                    locationTextView.text = "$animalLocation"
                    locationTextView.textSize = 24f // Yazı boyutunu ayarlayın
                    locationTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    locationTextView.gravity = Gravity.CENTER // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(locationTextView)

                    cardView.setOnClickListener {
                        showAdDetails(ad_id)
                    }

                    // ContainerLayout'a CardView'ı ekle

                    containerLayout.addView(cardView)



                }
            } catch (e: Exception) {
                // Hata durumunda işlemler
            }
        }
    }

    private fun showAdDetails(cardId: String) {
        // Kart tıklandığında yapılacak işlemleri buraya ekleyin
        // Örneğin, tıklanan kartın ID'sini kullanarak detay sayfasına yönlendirebilirsiniz.
        val intent = Intent(requireContext(), AdDetailActivity::class.java)
        intent.putExtra("cardId", cardId)
        startActivity(intent)
    }
}
