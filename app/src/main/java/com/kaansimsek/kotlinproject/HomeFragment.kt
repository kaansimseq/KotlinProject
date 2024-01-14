package com.kaansimsek.kotlinproject

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
                    val animalgender = document.getString("gender")
                    val animalrace = document.getString("race")
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
                    layoutParams.setMargins(0, 0, 0, 3)
                    cardView.layoutParams = layoutParams
                    cardView.cardElevation = 8f

                    // Horizontal LinearLayout oluştur
                    val horizontalLayout = LinearLayout(requireContext())
                    horizontalLayout.orientation = LinearLayout.HORIZONTAL
                    cardView.addView(horizontalLayout)

                    // ImageView oluştur ve ayarla
                    val imageView = ImageView(requireContext())
                    val imageLayoutParams = LinearLayout.LayoutParams(
                        350,
                        350,
                    )
                    imageLayoutParams.setMargins(124, 144, 144, 144) // Sağdan, soldan, yukarıdan, aşağıdan padding ayarlayın
                    // Yuvarlak kenarlığın oluşturulması
                    val shape = GradientDrawable()
                    shape.shape = GradientDrawable.OVAL
                    shape.setColor(Color.TRANSPARENT) // İçi boşaltılıyor, çünkü resmi yuvarlak kenarların içine koymak istiyoruz
                    shape.setStroke(5, Color.BLACK) // Kenarlığı siyah renkte, isteğe bağlı olarak ayarlanabilir

                    // ImageView'ın arkaplanını ve yuvarlak kenarı ayarlama
                    imageView.background = shape
                    imageView.clipToOutline = true
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
                    nameTextView.setPadding(0, 12, 0, 12)
                    nameTextView.text = "${animalName}"
                    nameTextView.textSize = 25f // Yazı boyutunu ayarlayın
                    nameTextView.setTypeface(null, Typeface.BOLD) // Kalın yapmak için setTypeface kullanın
                    nameTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    nameTextView.gravity = Gravity.LEFT // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(nameTextView)

                    val ageTextView = TextView(requireContext())
                    ageTextView.text = "· $animalAge years "
                    ageTextView.textSize = 18f // Yazı boyutunu ayarlayın
                    ageTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    ageTextView.gravity = Gravity.LEFT // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(ageTextView)

                    val genderTextView = TextView(requireContext())
                    genderTextView.text = "· $animalgender"
                    genderTextView.textSize = 18f // Yazı boyutunu ayarlayın
                    genderTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    genderTextView.gravity = Gravity.LEFT // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(genderTextView)

                    val raceTextView = TextView(requireContext())
                    raceTextView.text = "· $animalrace"
                    raceTextView.textSize = 18f // Yazı boyutunu ayarlayın
                    raceTextView.setTextColor(Color.parseColor("#6B5172")) // Renk kodunu ayarlayın
                    raceTextView.gravity = Gravity.LEFT // TextView'yi ortalamak için gravity ekleyin
                    textInfoLayout.addView(raceTextView)

                    val detailsButton = TextView(requireContext())

// Details butonunun genişlik ve yüksekliğini, içindeki metne göre otomatik olarak ayarlayın
                    detailsButton.text = "Details"
                    detailsButton.textSize = 18f
                    detailsButton.setTextColor(Color.parseColor("#FFFFFF"))
                    detailsButton.setBackgroundResource(R.drawable.green_border) // Kendi tasarımınıza uygun bir background ekleyebilirsiniz

// Soldan ve sağdan 8 dp'lik padding ayarlayın
                    detailsButton.setPadding(65, 5, 65, 5)
                    detailsButton.paddingRight
// Yazıyı ortalamak için gravity'yi ayarlayın
                    detailsButton.gravity = Gravity.CENTER

                    detailsButton.setOnClickListener {
                        // Details butonuna tıklandığında yapılacak işlemler
                        showAdDetails(ad_id)
                    }
                    val marginLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

// Sol, üst, sağ, alt sırasıyla margin değerlerini ayarla
                    marginLayoutParams.setMargins(0, 16, 8, 0)  // Örnekte sağa 16dp margin eklenmiştir

// LayoutParams'i TextView'e uygula
                    detailsButton.layoutParams = marginLayoutParams
                    textInfoLayout.addView(detailsButton)


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
