package com.example.imagerecog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.File

class ImageCaptured : AppCompatActivity() {
    var savedUri:Uri?=null
    var listIng= ArrayList<String>()
    var ingredientsText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_captured)

       // var savedUri: Uri? = intent.getParcelableExtra("imageUri")
        savedUri =intent.getParcelableExtra<Uri>("imageUri")

        val image: InputImage = InputImage.fromFilePath(applicationContext, savedUri)

        var photo=findViewById<ImageView>(R.id.photo)
        photo.setImageURI(savedUri)

        val recognizer = TextRecognition.getClient()



        var flag:Int = 0


        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // ...
                for (block in visionText.textBlocks) {

                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text
                    // Toast.makeText(baseContext, text, Toast.LENGTH_SHORT).show()
                    Log.i(toString(), text)
                    if (flag==100){
                        ingredientsText=text
                        break

                    }

                    for (line in block.lines) {
                        // ...
                        // val elementText = line.text
                        //  Toast.makeText(baseContext, elementText, Toast.LENGTH_SHORT).show()
                        /*if (line.text=="Ingredients") {
                            break
                        }*/
                        //Toast.makeText(baseContext, line.text, Toast.LENGTH_SHORT).show()
                        for (element in line.elements) {
                            val elementText = element.text
                            // Toast.makeText(baseContext, elementText, Toast.LENGTH_SHORT).show()
                            if (elementText == "INGREDIENTS:" || elementText == "Ingredients:") {
                                // Toast.makeText(baseContext, "Found ingredients", Toast.LENGTH_SHORT).show()
                                ingredientsText = text
                            }
                            if (ingredientsText=="Ingredients:"){
                                flag=100
                            }



                            // ...
                        }
                    }

                }

            /*    Toast.makeText(
                    baseContext, ingredientsText,
                    Toast.LENGTH_SHORT
                ).show()*/
                Log.i(String.toString(), ingredientsText)
                var text: String
                val finalText = ArrayList<String>()
                val t: Int = ingredientsText.length
                var i: Int = 0
                while (i != t) {
                    text = ""
                    for (k in i..t - 1) {

                        if (k>3&&ingredientsText[k - 1]==' '&&ingredientsText[k]=='a'&&ingredientsText[k + 1]=='n'&&ingredientsText[k + 2]=='d'&&(ingredientsText[k + 3]==' '||ingredientsText[k + 3].isUpperCase())){
                            break
                        }
                        if (k>3&&ingredientsText[k - 2]==' '&&ingredientsText[k - 1]=='a'&&ingredientsText[k]=='n'&&ingredientsText[k + 1]=='d'&&(ingredientsText[k + 2]==' '||ingredientsText[k + 2].isUpperCase())){
                            break
                        }
                        if (k>3&&ingredientsText[k - 3]==' '&&ingredientsText[k - 2]=='a'&&ingredientsText[k - 1]=='n'&&ingredientsText[k]=='d'&&(ingredientsText[k + 1]==' '||ingredientsText[k + 1].isUpperCase())){
                            break
                        }
                        if (ingredientsText[k] == ',') {
                            break
                        }

                        if (ingredientsText[k] == ' ') {
                            continue
                        }

                        if (ingredientsText[k] == '.') {
                            break
                        }

                        if (ingredientsText[k] == ':') {
                            break
                        }


                        text += ingredientsText[k]
                        i = k
                    }
                    i++
                    finalText.add(text)

                }
                val finalText2 = ArrayList<String>()
                finalText.remove(" ")
                var k = 0
                for (i in 0..finalText.size - 1) {
                    if (finalText[i] == "" || finalText[i] == "Ingredients") {
                        continue
                    }
                    finalText2.add(finalText[i])
                    k++
                }
                listIng = finalText2
                /*  for (i in finalText2) {
                     Toast.makeText(baseContext, i, Toast.LENGTH_SHORT).show()
                  }*/
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }

    }


    fun list()
    {
        val intent = Intent(this, MainActivity2::class.java)
        val bundle = Bundle()
        bundle.putSerializable("myList", listIng)
        bundle.putSerializable("inputList", ingredientsText)
        intent.putExtra("BUNDLE", bundle)
        val file: File = File(savedUri?.getPath())
        file.delete()
        if (file.exists()) {
            file.canonicalFile.delete()
            if (file.exists()) {
                applicationContext.deleteFile(file.name)
            }
        }
        startActivity(intent)

    }

    fun confirm(view: View) {
        list()
    }
    fun retake(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        val file: File = File(savedUri?.getPath())
        file.delete()
        if (file.exists()) {
            file.canonicalFile.delete()
            if (file.exists()) {
                applicationContext.deleteFile(file.name)
            }
        }
        startActivity(intent)
    }


}