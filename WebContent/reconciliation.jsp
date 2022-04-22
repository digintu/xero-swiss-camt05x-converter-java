<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="css/reconciliation.css"/>
    <title>Xero | CSV Import Options </title>
</head>
<body>
<div class="container">
    <h1 class="font-weight-bold">Xero Camt.053/054 Converter</h1>
    <form method="post">
        <div class="w-content">
            <div class="document">
                <h2>
                    Select account to feed
                </h2>
                <select class="accounts" id="accounts" onchange='changeAccount(this)'></select>

                <div id="currencyWarning" style="margin-bottom: 5%;margin-top: 5%;display: none">
                    <p style="font-size: 1.3em; color: red">
                        Warning: Some transaction do not have the same currency than the selected account, please convert the corresponding
                        amount to fix the issue or ignore if you think it is ok.
                    </p>
                </div>

                <div class="file">
                    <h2>
                        Statement lines imported from your file...
                    </h2>
                    <div class="form">
                        <div class="entries">
                            <span>Statement line <i
                                    id="currentPage">1</i> of <i>${ sessionScope.size }</i> </span>
                            <a href="#" onclick="changeEntryPosition(-1)">&#8592;
                                Previous</a>
                            <a href="#" onclick="changeEntryPosition(1)">Next
                                &#8594;</a>
                        </div>
                        <fieldset>
                            <legend>
                                Statement Lines
                            </legend>

                            <table class="data">
                                <thead>
                                <tr>
                                    <td colspan="2">Statement data...</td>
                                    <td class="no-border">Assign to...</td>
                                </tr>
                                </thead>
                                <tbody id="entries"></tbody>
                            </table>

                            <div class="checkbox option">
                                <div>
                                    <input name="skipImport" id="skipImport" type="checkBox" onchange="onSkipTransaction(this);">
                                </div>
                                <label for="skipImport">Do not import this transaction</label>
                            </div>
                        </fieldset>
                        <div class="actions">
                            <div class="right" id="buttons">
                                <a onclick="submitForm();" id="saveButton" class="successBtn" href="javascript:">
                                    Save
                                </a>
                                <a onclick="window.location = './import-file'" class="cancelBtn">Cancel</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="previews">
                    <h2>
                        Based on the statement line options you have assigned...
                    </h2>
                    <div id="mapping">
                        <table class="previews" id="previews"></table>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="./js/reconciliation.js"></script>
<script>
    var entries = ${ sessionScope.entries };
    var accounts = ${ sessionScope.accounts };
    var contacts = ${ sessionScope.contacts };
    var currentEntry = entries[0];
    var currencies = [...new Set(entries.map(entry => entry[2].value))];
    var account = null;
    const skipIndex = 6;

    displayCurrentEntry(currentEntry, account);
    displayAccounts(accounts);

    function changeEntryPosition(increment) {
        try {
            var currentPage = document.getElementById("currentPage");
            var currentPosition = Number(currentPage.innerHTML) + increment;
            if (currentPosition <= 0 || currentPosition > ${ sessionScope.size })
                return;
        } catch (e) {
            return;
        }

        document.getElementById("entries").innerHTML = "";
        currentPage.innerHTML = currentPosition;
        currentEntry = entries[currentPosition - 1];
        document.getElementById("skipImport").checked = (currentEntry[skipIndex].value)+"" == "true";
        displayCurrentEntry(currentEntry, account);
    }

    function onSkipTransaction(e) {
        currentEntry[skipIndex].value = e.checked ;
        entries[Number(document.getElementById("currentPage").innerHTML) - 1] = currentEntry;
    }

    function changeAccount(e) {
        if (e.value === '-1') {
            account = null;
        } else {
            account = accounts.find(a => a.accountID == e.value);
            displayCurrentEntry(currentEntry, account);
        }
    }

    function submitForm() {
        if (!account) {
            alert('Please select the account to feed');
            return;
        }
        document.getElementById("buttons").innerHTML = "<a class='successBtn'><div class='loader'></div></a>";
        var url = "reconciliation?accountID="+account.accountID;
        $.ajax({
            type: 'POST',
            url: url,
            type: "POST",
            dataType: 'json',
            data: JSON.stringify(entries),
            success: function (data) {
                document.getElementById("buttons").innerHTML =
                    "<a onclick='submitForm();' id='saveButton' class='successBtn'>Save</a>" +
                    "<a href='./import-file' class='cancelBtn'>Cancel</a>";
                alert('Success !');
                window.location.href = 'import-file';
            },
            error: function (status) {
                console.log(status);
                document.getElementById("buttons").innerHTML =
                    "<a onclick='submitForm();' id='saveButton' class='successBtn'>Save</a>" +
                    "<a href='./import-file' class='cancelBtn'>Cancel</a>";
                alert('Error occured, please try later...');
            }
        });
    }
</script>
</body>

</html>