package org.nuxeo.labs.hootsuite;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.blob.ManagedBlob;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.ecm.platform.picture.api.adapters.PictureResourceAdapter;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
@Operation(
        id=GetInfoToAttachAsset.ID,
        category="Hootsuite", label="Get Info To Attach Asset to a Post",
        description="Returns the parameters needed by hootsuite to attach the input asset to a social media post")
public class GetInfoToAttachAsset {

    public static final String ID = "Hootsuite.GetInfoToAttachAsset";

    @Param(name = "rendition", required = false)
    protected String rendition;

    @Param(name = "userID", required = true)
    protected String userID;

    @Context
    protected CoreSession session;

    @Context
    protected BlobManager blobManager;

    @Context
    protected MimetypeRegistry mimetypeRegistry;

    @OperationMethod
    public Blob run(DocumentModel doc) throws JSONException, IOException, NoSuchAlgorithmException {

        Blob blob = null;

        JSONObject hootsuiteParams = new JSONObject();

        if (doc.hasFacet("Picture")) {
            PictureResourceAdapter picture = doc.getAdapter(PictureResourceAdapter.class);
            blob = picture.getPictureFromTitle("FullHD");
        } else if (doc.hasFacet("Video")) {
            VideoDocument videoDocument = doc.getAdapter(VideoDocument.class);
            blob = videoDocument.getTranscodedVideo("MP4 1080p").getBlob();
        } else {
            hootsuiteParams.put("error","Input document is not an asset");
        }

        if (blob instanceof ManagedBlob) {
            BlobProvider blobProvider = blobManager.getBlobProvider(blob);
            URI url = blobProvider.getURI((ManagedBlob) blob, BlobManager.UsageHint.DOWNLOAD,null);
            if (url != null) {
                hootsuiteParams.put("url",url.toString());
                hootsuiteParams.put("name",blob.getFilename());
                hootsuiteParams.put("extension",mimetypeRegistry.getExtensionsFromMimetypeName(blob.getMimeType()).get(0));
                setToken(hootsuiteParams);
            } else {
                hootsuiteParams.put("error","Cannot get a direct download URL for input document");
            }
        } else {
            hootsuiteParams.put("error","Cannot get a direct download URL for input document");
        }

        StringBlob result = new StringBlob(hootsuiteParams.toString());
        result.setMimeType("application/json");
        return result;
    }


    protected void setToken(JSONObject object) throws NoSuchAlgorithmException, JSONException {

        MessageDigest md = MessageDigest.getInstance("SHA-512");

        long ts = System.currentTimeMillis() / 1000;
        object.put("timestamp",ts);

        String secret = Framework.getProperty("hootsuite.secret");

        String message = userID + ts + object.getString("url") + secret;

        byte[] digest = md.digest(message.getBytes());

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }

        String token = sb.toString();

        System.out.println("Token Hex format : " + token);

        object.put("token",token);

    }


}
