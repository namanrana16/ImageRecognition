package com.example.imagerecog

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        tfLiteInterpreter = Interpreter( loadModelFile() )

        // Start vocab processing, show a ProgressDialog to the user.
        val progressDialog = ProgressDialog( this )
        progressDialog.setMessage( "Parsing word_dict.json ..." )
        progressDialog.setCancelable( false )
        progressDialog.show()
        classifier.processVocab( object: Classifier.VocabCallback {
            override fun onVocabProcessed() {
                // Processing done, dismiss the progressDialog.
                progressDialog.dismiss()
            }
        })



        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        val list = args!!.getSerializable("myList") as ArrayList<String>

        recyclerView.layoutManager= LinearLayoutManager(this)
        val finalList = "wheat"

        finalList.trim()
        Log.i("final", finalList)
       if ( !TextUtils.isEmpty( finalList ) ){
            // Tokenize and pad the given input text.
            val tokenizedMessage = classifier.tokenize( finalList )
            val paddedMessage = classifier.padSequence( tokenizedMessage )

            val results = classifySequence( paddedMessage )
            val rating  = results.indexOf(results.max()!!)
            result_text.text = "RATING IS ${rating+1}"
        }
        else{
            Toast.makeText( this@MainActivity2, "Please enter a message.", Toast.LENGTH_LONG).show();
        }

        //  val items = fetchData()
        val adaptor= IngredientsListAdaptor(list as ArrayList<String>, this)
        recyclerView.adapter=adaptor


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
        val inputs : Array<FloatArray> = arrayOf( sequence.map { it.toFloat() }.toFloatArray() )
        // Output shape -> ( 1 , 2 ) ( as numClasses = 2 )
        val outputs : Array<FloatArray> = arrayOf( FloatArray( 10 ) )
        tfLiteInterpreter?.run( inputs , outputs )
        return outputs[0]
    }

  /* private fun fetchData():ArrayList<String>{
        val list = ArrayList<String>()
        for ( i in 0 until 100){
            list.add("Item $i")
        }
        return list
    }*/

    override fun onItemClicked(item: String) {
        Toast.makeText(this, "Item clicked is $item", Toast.LENGTH_SHORT).show()
    }


}