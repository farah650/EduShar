import { useEffect, useState } from "react";
import { getAllVideos } from "../api/videos";
import VideoCard from "../components/VideoCard";

export default function Home() {
  const [videos, setVideos] = useState([]);

  useEffect(() => {
    getAllVideos().then((res) => {
      setVideos(res.data || []);
    }).catch((err) => {
      console.error(err);
    });
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Toutes les vidéos</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {videos.map((v) => (
          <VideoCard key={v.id || v.videoId} video={v} />
        ))}
      </div>
    </div>
  );
}
