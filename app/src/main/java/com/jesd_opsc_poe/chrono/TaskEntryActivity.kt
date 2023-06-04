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
    private lateinit var categoriesDropdown: AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_entry)

        auth = Firebase.auth
        selectedClient = "" //initialised empty for 'isNotEmpty' check in ibtnAddCategory.setOnClickListener()

        ibtnAddClient = findViewById(R.id.ibtnAddClient)
        ibtnAddCategory = findViewById(R.id.ibtnAddCategory)

        //clients dropdown is set and user input is blocked
        clientsDropdown = findViewById(R.id.txtSelectClient)
        clientsDropdown.inputType = InputType.TYPE_NULL
        //list of the current user's clients are added to dropdown
        populateClientsDropdown()

        //categories dropdown is set and user input is blocked
        categoriesDropdown = findViewById(R.id.txtSelectCategory)
        categoriesDropdown.inputType = InputType.TYPE_NULL

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

        clientsDropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                //selected client is set to the clicked item in the 'clients' dropdown
                selectedClient = parent.getItemAtPosition(position) as String
                //categories dropdown is cleared when client changes
                categoriesDropdown.text.clear()
                //categories dropdown is populated with categories under the selected client of the current user
                populateCategoriesDropdown()
                Toast.makeText(this, "selected client: $selectedClient", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateClientsDropdown() {
        getClientNamesForCurrentUser { clientNames ->
            val clientArrayAdapter = ArrayAdapter(this, R.layout.item_dropdown, clientNames)
            clientsDropdown.setAdapter(clientArrayAdapter)
        }
    }

    private fun populateCategoriesDropdown() {
        getCategoryNamesForSelectedClient { categoryNames ->
            val categoryArrayAdapter = ArrayAdapter(this, R.layout.item_dropdown, categoryNames)
            categoriesDropdown.setAdapter(categoryArrayAdapter)
        }
    }

    private fun addClient() {
        val dbClientsRef = FirebaseDatabase.getInstance().getReference("Clients")
        val query = dbClientsRef.orderByChild("userClientKey")
            .equalTo("${auth.currentUser?.email}_${newClientName}")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Client already exists under current user
                    Toast.makeText(
                        this@TaskEntryActivity,
                        "Client already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //combined key for client
                    val clientKey = dbClientsRef.push().key
                    val categoryData = mapOf(
                        "userClientKey" to "${auth.currentUser?.email}_${newClientName}",
                        "userKey" to auth.currentUser?.email.toString(),
                        "clientName" to newClientName.trim()
                    )
                    clientKey?.let {
                        dbClientsRef.child(it).setValue(categoryData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@TaskEntryActivity,
                                    "Client added",
                                    Toast.LENGTH_SHORT
                                ).show()
                                populateClientsDropdown()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@TaskEntryActivity,
                                    "Failed to add client",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TaskEntryActivity, "Add client: database error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addCategory() {
        val dbCategoriesRef = FirebaseDatabase.getInstance().getReference("Categories")
        val query = dbCategoriesRef.orderByChild("categoryKey")
            .equalTo("${auth.currentUser?.email}_${selectedClient}_$newCategoryName")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Category already exists for the selected client and current user
                    Toast.makeText(
                        this@TaskEntryActivity,
                        "Category already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val categoryKey = dbCategoriesRef.push().key
                    val categoryData = mapOf(
                        "categoryKey" to "${auth.currentUser?.email}_${selectedClient}_$newCategoryName",
                        "clientKey" to "${auth.currentUser?.email}_${selectedClient}",
                        "userKey" to auth.currentUser?.email.toString(),
                        "categoryName" to newCategoryName.trim()
                    )
                    categoryKey?.let {
                        dbCategoriesRef.child(it).setValue(categoryData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@TaskEntryActivity,
                                    "Category added",
                                    Toast.LENGTH_SHORT
                                ).show()
                                populateCategoriesDropdown()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@TaskEntryActivity,
                                    "Failed to add category",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TaskEntryActivity, "Add category: database error", Toast.LENGTH_LONG).show()
            }
        })
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

        //database reference to 'Clients' node
        val clientsRef = FirebaseDatabase.getInstance().getReference("Clients")

        //Array to store the names of the current user's clients
        val clientNames: ArrayList<String> = ArrayList()

        //Query that searches the database reference ('Clients' node) for clients, where userRef = current user's email
        clientsRef.orderByChild("userKey").equalTo(auth.currentUser?.email.toString())
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (clientSnapshot in dataSnapshot.children) {
                        //client names are retrieved from the clients and added to the clientNames array
                        val clientName =
                            clientSnapshot.child("clientName").getValue(String::class.java)
                        clientName?.let { clientNames.add(it) }
                    }
                    //returns array
                    callback(clientNames)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //no clients retrieved, blank array returned
                    callback(clientNames)
                }
            })
    }

    private fun getCategoryNamesForSelectedClient(callback: (ArrayList<String>) -> Unit) {

        //database reference to 'Categories' node
        val clientsRef = FirebaseDatabase.getInstance().getReference("Categories")

        //Array to store the names of the selected client's categories
        val categoryNames: ArrayList<String> = ArrayList()

        //Query that searches the database reference ('Clients' node) for clients, where userRef = current user's email
        clientsRef.orderByChild("clientKey").equalTo("${auth.currentUser?.email}_${selectedClient}")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (categorySnapshot in dataSnapshot.children) {
                        //category names are retrieved from the categories and added to the categoryNames array
                        val categoryName =
                            categorySnapshot.child("categoryName").getValue(String::class.java)
                        categoryName?.let { categoryNames.add(it) }
                    }
                    //returns array
                    callback(categoryNames)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    //no categories retrieved, blank array returned
                    callback(categoryNames)
                }
            })
    }
}