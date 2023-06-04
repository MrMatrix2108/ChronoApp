package com.jesd_opsc_poe.chrono

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class TaskEntryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ibtnAddClient: ImageButton
    private lateinit var ibtnAddCategory: ImageButton
    private lateinit var newClientName: String
    private lateinit var newCategoryName: String
    private lateinit var selectedClient: String
    private lateinit var clientsDropdown: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_entry)

        auth = Firebase.auth
        selectedClient = ""

        ibtnAddClient = findViewById(R.id.ibtnAddClient)
        ibtnAddCategory = findViewById(R.id.ibtnAddCategory)

        clientsDropdown = findViewById(R.id.txtSelectClient)
        clientsDropdown.inputType = InputType.TYPE_NULL
        populateClientsDropdown()

        ibtnAddClient.setOnClickListener {
            showInputDialog(true)
        }

        ibtnAddCategory.setOnClickListener {
            if (selectedClient.isNotEmpty()) {
                showInputDialog(false)
            } else {
                Toast.makeText(this, "Client not selected", Toast.LENGTH_SHORT).show()
            }
        }

        clientsDropdown.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            selectedClient = parent.getItemAtPosition(position) as String
            Toast.makeText(this , "selected client: $selectedClient", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateClientsDropdown() {
        getClientNamesForCurrentUser { clientNames ->
            val clientArrayAdapter = ArrayAdapter(this, R.layout.item_dropdown, clientNames)
            clientsDropdown.setAdapter(clientArrayAdapter)
        }
    }

    private fun addClient() {
        //database reference to 'Clients' node
        val dbClientsRef = FirebaseDatabase.getInstance().getReference("Clients")
        //generates a unique key for the Client entry
        val clientKey = dbClientsRef.push().key

        //map of data to go under client key
        val clientData = mapOf(
            "userRef" to auth.currentUser?.email,
            "clientName" to newClientName.trim()
        )

        //database query that adds client data under the client key, in the 'Clients' node.
        clientKey?.let {
            dbClientsRef.child(it).setValue(clientData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Client added", Toast.LENGTH_SHORT).show()
                    //the 'clients' dropdown is repopulated with the updated array of clients.
                    populateClientsDropdown()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add client", Toast.LENGTH_SHORT).show()
                }
            newClientName = ""
        }
    }

    private fun addCategory() { //adds a category under the selected client
        //database reference to 'Categories' node
        val dbClientsRef = FirebaseDatabase.getInstance().getReference("Categories")
        //generates a unique key for the Category entry
        val categoryKey = dbClientsRef.push().key

        //map of data to go under category key
        val categoryData = mapOf(
            "userRef" to auth.currentUser?.email,
            "clientRef" to selectedClient, //client currently selected in 'Select Client' dropdown
            "categoryName" to newCategoryName.trim()
        )

        //database query that adds category data under the category key, in the 'Categories' node.
        categoryKey?.let {
            dbClientsRef.child(it).setValue(categoryData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
                }
            newClientName = ""
        }
    }

    private fun showInputDialog(isClient: Boolean) { //collects data for client (isClient) or category !(isClient)
        val builder = AlertDialog.Builder(this)
        if (isClient) {
            builder.setTitle("New Client")
        } else {
            builder.setTitle("New Category for $selectedClient")
        }
        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            // Handling the input value
            if (isClient) {
                newClientName = input.text.toString()
                if (newClientName.isNotEmpty() && HelperClass.notAllSpaces(newClientName)) {
                    addClient()
                } else {
                    Toast.makeText(this, "Invalid client name", Toast.LENGTH_SHORT).show()
                }
            } else {
                newCategoryName = input.text.toString()
                if (newCategoryName.isNotEmpty() && HelperClass.notAllSpaces(newCategoryName)) {
                    addCategory()
                } else {
                    Toast.makeText(this, "Invalid category name", Toast.LENGTH_SHORT).show()
                }
            }

        }

        builder.setNegativeButton("Cancel") { _, _ ->
            // Handling cancel action
            newClientName = ""
            newCategoryName = ""
        }
        builder.show()
    }

    private fun getClientNamesForCurrentUser(callback: (ArrayList<String>) -> Unit) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        val database = FirebaseDatabase.getInstance()
        val clientsRef = database.getReference("Clients")

        val clientNames: ArrayList<String> = ArrayList()

        clientsRef.orderByChild("userRef").equalTo(currentUserEmail).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (clientSnapshot in dataSnapshot.children) {
                    val clientName = clientSnapshot.child("clientName").getValue(String::class.java)
                    clientName?.let { clientNames.add(it) }
                }
                callback(clientNames)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //could not retrieve client names error?
                callback(clientNames)
            }
        })
    }
}