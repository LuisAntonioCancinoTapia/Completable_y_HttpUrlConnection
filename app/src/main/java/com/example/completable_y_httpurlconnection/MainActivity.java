package com.example.completable_y_httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText inputBook;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookDescription;
    private ImageView bookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBook = (EditText)findViewById(R.id.inputbook);
        bookTitle = (TextView)findViewById(R.id.booktitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        bookDescription = (TextView)findViewById(R.id.bookDescription);
        bookImage = (ImageView) findViewById(R.id.bookImage);
    }

    public void searchBook(View view) {
        String searchString = inputBook.getText().toString();

        new GetBook(bookTitle,bookAuthor,bookDescription,bookImage).execute(searchString);
    }

    public class GetBook extends AsyncTask<String,Void,String> {

        private WeakReference<TextView> mTextTitle;
        private WeakReference<TextView> mTextAuthor;
        private WeakReference<TextView> mTextDescription;
        private WeakReference<ImageView> mImageBook;


        public GetBook(TextView mTextTitle, TextView mTextAuthor, TextView mTextDescription, ImageView mImagenBook) {
            this.mTextTitle = new WeakReference<>(mTextTitle);
            this.mTextAuthor = new WeakReference<>(mTextAuthor);
            this.mTextDescription = new WeakReference<>(mTextDescription);
            this.mImageBook = new WeakReference<>(mImagenBook);
        }

        @Override
        protected String doInBackground(String... strings) {
            return NetUtilities.getBookInfo(strings[0]);
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i = 0;
                String title = null;
                String author = null;
                String description = null;
                String image = null;

                while (i < itemsArray.length() && (title == null && author == null)){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject imageInfo = volumeInfo.getJSONObject("imageLinks");
                    try {
                        title = volumeInfo.getString("title");
                        author = volumeInfo.getString("authors");
                        description = volumeInfo.getString("description");
                        image = imageInfo.getString("thumbnail");

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    i++;
                }
                if (title != null && author != null){
                    mTextTitle.get().setText(title);
                    mTextAuthor.get().setText(author);
                    mTextDescription.get().setText(description);
                    Picasso.with(MainActivity.this).load(image).into(bookImage);

                }else {
                    mTextTitle.get().setText("No existen resultados para la consulta");
                    mTextAuthor.get().setText("");
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}