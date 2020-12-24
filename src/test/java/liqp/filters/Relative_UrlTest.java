package liqp.filters;

import liqp.ParseSettings;
import liqp.Template;
import liqp.parser.Flavor;
import liqp.parser.Inspectable;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
public class Relative_UrlTest {

    /**
     * should "produce a relative URL from a page URL" do
     *  page_url = "/about/my_favorite_page/"
     *  assert_equal "/base#{page_url}", @filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testProduceARelativeURLFromAPageURL() {
        String res = Template.parse("{{ '/about/my_favorite_page/' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base/about/my_favorite_page/", res);
    }

    /**
     should "ensure the leading slash between baseurl and input" do
     page_url = "about/my_favorite_page/"
     assert_equal "/base/#{page_url}", @filter.relative_url(page_url)
     end
     */
    @Test
    public void testEnsureTheLeadingSlashBetweenBaseurlAndInput() {
        String res = Template.parse("{{ 'about/my_favorite_page/' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base/about/my_favorite_page/", res);
    }

    /**
     should "ensure the leading slash for the baseurl" do
     page_url = "about/my_favorite_page/"
     filter = make_filter_mock("baseurl" => "base")
     assert_equal "/base/#{page_url}", filter.relative_url(page_url)
     end
     */
    @Test
    public void testEnsureTheLeadingSlashForTheBaseurl() {
        String res = Template.parse("{{ 'about/my_favorite_page/' | relative_url }}", jekyll())
                .render(getData("base"));
        assertEquals("/base/about/my_favorite_page/", res);
    }

    @Test
    public void testNormalizeInternationalURLs() {
        String res = Template.parse("{{ '错误.html' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base/%E9%94%99%E8%AF%AF.html", res);
    }

    /**
     *
     * should "be ok with a nil 'baseurl'" do
     *  page_url = "about/my_favorite_page/"
     *  filter = make_filter_mock(
     *  "url"     => "http://example.com",
     *  "baseurl" => nil
     *  )
     *  assert_equal "/#{page_url}", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testBeOkWithANilBaseurl() {
        String res = Template.parse("{{ 'about/my_favorite_page/' | relative_url }}", jekyll())
                .render(Collections.singletonMap("baseurl", null));
        assertEquals("/about/my_favorite_page/", res);
    }

    /**
     * should "not prepend a forward slash if input is empty" do
     *  page_url = ""
     *  filter = make_filter_mock(
     *  "url"     => "http://example.com",
     *  "baseurl" => "/base"
     *  )
     *  assert_equal "/base", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testShouldNotPrependAForwardSlashIfInputIsEmpty() {
        String res = Template.parse("{{ '' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base", res);
    }

    /**
     * should "not prepend a forward slash if baseurl ends with a single '/'" do
     *  page_url = "/css/main.css"
     *  filter = make_filter_mock(
     *  "url"     => "http://example.com",
     *  "baseurl" => "/base/"
     *  )
     *  assert_equal "/base/css/main.css", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testShouldNotPrependAForwardSlashIfBaseurlEndsWithASingleOne() {
        String res = Template.parse("{{ '/css/main.css' | relative_url }}", jekyll())
                .render(getData("/base/"));
        assertEquals("/base/css/main.css", res);
    }

    /**
     * should "not return valid URI if baseurl ends with multiple '/'" do
     *  page_url = "/css/main.css"
     *  filter = make_filter_mock(
     *  "url"     => "http://example.com",
     *  "baseurl" => "/base//"
     *  )
     *  refute_equal "/base/css/main.css", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testShouldNotReturnValidURIIfBaseurlEndsWithMultipleOnes() {
        String res = Template.parse("{{ '/css/main.css' | relative_url }}", jekyll())
                .render(getData("/base//"));
        assertEquals("/base/css/main.css", res);
    }
    /**
     *  should "not prepend a forward slash if both input and baseurl are simply '/'" do
     *  page_url = "/"
     *  filter = make_filter_mock(
     *  "url"     => "http://example.com",
     *  "baseurl" => "/"
     *  )
     *  assert_equal "/", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testNotPrependAForwardSlashIfBothInputAndBaseurlAreSimplySlashes() {
        String res = Template.parse("{{ '/' | relative_url }}", jekyll())
                .render(getData("/"));
        assertEquals("/", res);
    }

    /**
     *  should "transform the input baseurl to a string" do
     *  page_url = "/my-page.html"
     *  filter = make_filter_mock("baseurl" => Value.new(proc { "/baseurl/" }))
     *  assert_equal "/baseurl#{page_url}", filter.relative_url(page_url)
     *  end
     */
    @Test
    public void testsShouldTransformTheInputBaseurlToAString() {
        java.util.Map<String, Object> data = new HashMap<>();
        //noinspection unused
        data.put("site", new Inspectable() {
            public final String baseurl = "/baseurl/";
        });

        String res = Template.parse("{{ '/my-page.html' | relative_url }}", jekyll())
                .render(data);
        assertEquals("/baseurl/my-page.html", res);
    }
    /**
     *  should "transform protocol-relative url" do
     *  url = "//example.com/"
     *  assert_equal "/base//example.com/", @filter.relative_url(url)
     *  end
     */
    @Test
    public void testShouldTransformProtocolRelativeUrl() {
        String res = Template.parse("{{ '//example.com/' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base/example.com/", res);
    }

    /**
     * should "not modify an absolute url with scheme" do
     *  url = "file:///file.html"
     *  assert_equal url, @filter.relative_url(url)
     *  end
     */
    @Test
    public void testShouldNotModifyAnAbsoluteUrlWithScheme() {
        String res = Template.parse("{{ 'file:///file.html' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("file:///file.html", res);
    }

    /**
     *  should "not normalize absolute international URLs" do
     *  url = "https://example.com/错误"
     *  assert_equal "https://example.com/错误", @filter.relative_url(url)
     *  end
     */
    @Test
    public void testShouldNotNormalizeAbsoluteInternationalURLs() {
        String res = Template.parse("{{ 'https://example.com/错误' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("https://example.com/错误", res);
    }

    @Test
    public void testWithFullyFeaturedUrl() {
        String res = Template.parse("{{ '/some/path?with=extra&parameters=true#anchorhere' | relative_url }}", jekyll())
                .render(getData("/base"));
        assertEquals("/base/some/path?with=extra&parameters=true#anchorhere", res);
    }

    // site.baseurl
    private Map<String, Object> getData(String s) {
        Map<String, Object> siteMap = Collections.singletonMap("baseurl", (Object) s);
        return Collections.singletonMap("site", (Object)siteMap);
    }

    private ParseSettings jekyll() {
        return new ParseSettings.Builder().withFlavor(Flavor.JEKYLL).build();
    }
}
