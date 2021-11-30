package com.example.imagerecog




import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*


class MainActivity2 : AppCompatActivity(), IngredientItemClicked {

    private val MODEL_ASSETS_PATH = "bp.tflite"

    // Max Length of input sequence. The input shape for the model will be ( None , INPUT_MAXLEN ).
    private val INPUT_MAXLEN = 100

    private var tfLiteInterpreter : Interpreter? = null

    val classifier = Classifier( this , "word_dict.json" , INPUT_MAXLEN )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        val list = args!!.getSerializable("myList") as ArrayList<String>
        var inputList= args!!.getSerializable("inputList") as String

        recyclerView.layoutManager= LinearLayoutManager(this)
        var finallist:String=""
        // var avg:Float= 0F
        for (i in list){
            finallist+=i
            finallist+=" "
        }
        var inputListFinal:String=""

        val len = inputList.length
        for (i in inputList){
            if (i.isUpperCase()){
                inputListFinal+=i.toLowerCase()
                continue
            }
            if (i==','||i=='('||i==')'||i.isDigit()||i==':'||i=='.') continue
            inputListFinal+=i
        }
        inputListFinal.trim()


        var flag:Int = 0
        //avg/=list.size
        // var df = DecimalFormat("#.##")
        // avg=df.format(avg).toFloat()
        //    tfLiteInterpreter = Interpreter( loadModelFile() )

        tfLiteInterpreter = Interpreter( loadModelFile() )

        // Start vocab processing, show a ProgressDialog to the user.
        val progressDialog = ProgressDialog( this )
        progressDialog.setMessage( "Parsing word_dict.json ..." )
        progressDialog.setCancelable( false )
        progressDialog.show()
        classifier.processVocab( object: Classifier.VocabCallback {
            override fun onVocabProcessed() {
                // Processing done, dismiss the progressDialog.
                flag=1
                progressDialog.dismiss()
            }
        })


        Handler(Looper.getMainLooper()).postDelayed({
            val adaptor= IngredientsListAdaptor(list, this)
            val ratingText=findViewById<TextView>(R.id.ratingText)
            recyclerView.adapter=adaptor

            if ( !TextUtils.isEmpty( inputListFinal ) ){
                // Tokenize and pad the given input text.
                Log.i("FINALLLL", inputListFinal)
                val tokenizedMessage = classifier.tokenize( inputListFinal )
                val paddedMessage = classifier.padSequence( tokenizedMessage )

                val results = classifySequence( paddedMessage )
                val rating  = results.indexOf(results.max()!!)
//                Toast.makeText(this,inputListFinal,Toast.LENGTH_LONG
//                ).show()

                ratingText.text = "${rating+1}/10"

              if(rating>=5)
                    ratingText.setTextColor(Color.parseColor("#34FE3C"))
                else if(rating<5)
                    ratingText.setTextColor(Color.parseColor("#FE3434"))

            }
            else{
                Toast.makeText( this@MainActivity2, "No Ingredients Detected,Try Again", Toast.LENGTH_LONG).show()
            }

        }, 1000)




        /*  for (i in list) {
              Toast.makeText(baseContext, "I found $i", Toast.LENGTH_SHORT).show()
          }*/
        //  val items = fetchData()



        //   ratingText.text="$avg/10"
        /*   if (avg>5){
               ratingText.setTextColor(Color.parseColor("#00FF00"))
           }
           else {
               ratingText.setTextColor(Color.parseColor("#FF0000"))
           }*/


    }


    override fun onItemClicked(item: String) {
        Toast.makeText(this, "Item clicked is $item", Toast.LENGTH_SHORT).show()
    }
    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd(MODEL_ASSETS_PATH)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Perform inference, given the input sequence.
    private fun classifySequence (sequence : IntArray ): FloatArray {
        // Input shape -> ( 1 , INPUT_MAXLEN )
        val inputs: Array<FloatArray> = arrayOf(sequence.map { it.toFloat() }.toFloatArray())
        // Output shape -> ( 1 , 2 ) ( as numClasses = 2 )
        val outputs: Array<FloatArray> = arrayOf(FloatArray(10))
        tfLiteInterpreter?.run(inputs, outputs)
        return outputs[0]
    }



}